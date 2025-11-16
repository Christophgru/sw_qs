use itertools::Itertools;
use rand::seq::{IteratorRandom, SliceRandom};
use rand::{Rng, random_range, rng};
use std::cmp::Ordering;
use std::collections::HashSet;
use std::error::Error;
use std::fs::File;
use std::hash::Hash;
use std::io::{self, BufRead, BufReader};
use std::path::Path;
use std::vec;

#[derive(Debug, Clone, PartialEq, Eq, Hash, PartialOrd, Ord)]
struct Testcase {
    name: String,
    lines: Vec<u16>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
struct Individual {
    tests: Vec<Testcase>,
    covered_lines: Vec<u16>,
}
impl Ord for Individual {
    fn cmp(&self, other: &Self) -> Ordering {
        self.tests.len().cmp(&other.tests.len())
    }
}

impl PartialOrd for Individual {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

fn read_testcases<P: AsRef<Path>>(path: P) -> Result<Vec<Testcase>, Box<dyn Error>> {
    let file = File::open(path)?;
    let reader = BufReader::new(file);

    let mut testcases: Vec<Testcase> = Vec::new();
    let mut current: Option<Testcase> = None;

    for line_result in reader.lines() {
        let line = line_result?;
        let line = line.trim();

        // Ignore empty lines
        if line.is_empty() {
            continue;
        }

        // Lines beginning with a letter mark the next testcase
        if line.chars().next().unwrap().is_alphabetic() {
            // If there is a unfinished testcase, finish it
            if let Some(tc) = current.take() {
                testcases.push(tc);
            }
            current = Some(Testcase {
                name: line.to_string(),
                lines: Vec::new(),
            });
        } else {
            // Else add number to current testcase
            let value: u16 = line.parse()?;
            match &mut current {
                Some(tc) => tc.lines.push(value),
                None => {
                    return Err(io::Error::new(
                        io::ErrorKind::InvalidData,
                        format!("Zahl '{line}' gefunden, bevor ein Testname definiert wurde"),
                    )
                    .into());
                }
            }
        }
    }

    // Last testcase
    if let Some(tc) = current {
        testcases.push(tc);
    }

    Ok(testcases)
}

fn read_changed_lines<P: AsRef<Path>>(path: P) -> Result<Vec<Vec<u16>>, Box<dyn Error>> {
    let file: File = File::open(path)?;
    let reader: BufReader<File> = BufReader::new(file);

    let mut result: Vec<Vec<u16>> = vec![];

    for line_result in reader.lines() {
        let line: String = line_result?;
        let line: &str = line.trim();

        // Ignore empty lines
        if line.is_empty() {
            continue;
        }
        result.push(
            line.split_whitespace()
                .skip(1)
                .map(|s| s.parse::<u16>().unwrap())
                .collect::<Vec<u16>>(),
        );
    }

    Ok(result)
}

fn copy_x_distinct<T: Clone + Eq + Hash>(src: &Vec<T>, x: usize) -> Vec<T> {
    let mut seen: HashSet<&T> = HashSet::new();
    let mut result: Vec<T> = Vec::new();

    for item in src {
        if seen.insert(item) {
            result.push(item.clone());
            if result.len() == x {
                break;
            }
        }
    }

    result
}

fn init_population(population: &mut Vec<Individual>, testcases: &Vec<Testcase>) {
    while population.len() < 100 {
        let size: usize = random_range(1..16);
        let tc = copy_x_distinct(&testcases, size);
        let ind = Individual {
            tests: tc.clone(),
            covered_lines: calculate_covered_lines(&tc),
        };
        population.push(ind);
    }
}

fn lines_covered(individual: Individual, lines: &Vec<u16>) -> bool {
    lines
        .iter()
        .all(|&x| individual.covered_lines.iter().any(|&item| item == x))
}

fn calculate_covered_lines(testcases: &Vec<Testcase>) -> Vec<u16> {
    testcases
        .iter()
        .flat_map(|t| t.lines.iter()) // all lines of all testcases
        .copied()
        .unique() // remove dupes
        .sorted()
        .collect()
}

fn mutate(individual: &mut Individual, testcases: &Vec<Testcase>) {
    let mut rng = rng();

    // helper: is testcase already part of individual?
    let contains_test =
        |cand: &Testcase, tests: &Vec<Testcase>| tests.iter().any(|t| t.name == cand.name);

    #[derive(Copy, Clone)]
    enum Op {
        Add,
        Remove,
        Replace,
    }

    let mut ops = Vec::new();

    if !individual.tests.is_empty() {
        ops.push(Op::Remove);
    }

    let unused_exists = testcases
        .iter()
        .any(|tc| !contains_test(tc, &individual.tests));

    if unused_exists {
        ops.push(Op::Add);
    }

    if !individual.tests.is_empty() && unused_exists {
        ops.push(Op::Replace);
    }

    if ops.is_empty() {
        return;
    }

    let op = ops[rng.random_range(0..ops.len())];

    match op {
        Op::Add => {
            // choose random testcase currently not present in the individual
            if let Some(new_tc) = testcases
                .iter()
                .filter(|tc| !contains_test(tc, &individual.tests))
                .choose(&mut rng)
            {
                individual.tests.push(new_tc.clone());
            }
        }
        Op::Remove => {
            let len = individual.tests.len();
            if len > 0 {
                let idx = rng.random_range(0..len);
                individual.tests.remove(idx);
            }
        }
        Op::Replace => {
            let len = individual.tests.len();
            if len > 0 {
                let idx = rng.random_range(0..len);
                let current_name = individual.tests[idx].name.clone();

                if let Some(new_tc) = testcases
                    .iter()
                    .filter(|tc| tc.name != current_name && !contains_test(tc, &individual.tests))
                    .choose(&mut rng)
                {
                    individual.tests[idx] = new_tc.clone();
                }
            }
        }
    }

    // recalculate covered_lines
    individual.covered_lines = calculate_covered_lines(&individual.tests)
}

fn mutate_x_percent(
    population: &mut Vec<Individual>,
    testcases: &Vec<Testcase>,
    percentage: f32,
    elite_percentage: f32,
) {
    let len = population.len();
    if len == 0 {
        return;
    }

    // number of elements to mutate (at least 1 at most len())
    let to_mutate = ((len as f32) * percentage).round() as usize;
    let to_mutate = to_mutate.clamp(1, len);

    let elites = ((len as f32) * elite_percentage).round() as usize;
    let elites = elites.clamp(1, len);

    let mut rng = rng();

    // shuffle indices (leave elites untouched)
    let mut indices: Vec<usize> = (elites..len).collect();
    indices.shuffle(&mut rng);

    for &i in indices.iter().take(to_mutate) {
        mutate(&mut population[i], testcases);
    }
}

// cut two individuals in half and stick them together again
fn crossover(parent1: &Individual, parent2: &Individual) -> (Individual, Individual) {
    let mut rng = rng();

    if parent1.tests.is_empty() || parent2.tests.is_empty() {
        return (parent1.clone(), parent2.clone());
    }

    let len1 = parent1.tests.len();
    let len2 = parent2.tests.len();

    let cut1 = rng.random_range(0..=len1);
    let cut2 = rng.random_range(0..=len2);

    fn build_child(a: &[Testcase], b: &[Testcase]) -> Individual {
        let mut seen = HashSet::new();
        let mut tests = Vec::new();

        for tc in a.iter().chain(b.iter()) {
            if seen.insert(tc.name.clone()) {
                tests.push(tc.clone());
            }
        }

        let covered_lines = calculate_covered_lines(&tests);
        Individual {
            tests,
            covered_lines,
        }
    }

    let child1 = build_child(&parent1.tests[..cut1], &parent2.tests[cut2..]);

    let child2 = build_child(&parent2.tests[..cut2], &parent1.tests[cut1..]);

    (child1, child2)
}

fn crossover_x_percent_with_elite(
    population: &mut Vec<Individual>,
    percentage: f32,
    elite_percentage: f32,
) {
    let len = population.len();

    let mut rng = rng();

    let elites = ((len as f32) * elite_percentage).round() as usize;
    let elites = elites.clamp(1, len);

    let mut indices: Vec<usize> = (elites..len).collect();
    indices.shuffle(&mut rng);

    let max_candidates = indices.len();
    if max_candidates < 2 {
        return;
    }

    let mut to_use = ((len as f32) * percentage).round() as usize;
    to_use = to_use.clamp(2, max_candidates);

    if to_use % 2 == 1 {
        to_use -= 1;
    }
    if to_use < 2 {
        return;
    }

    for pair in indices.iter().take(to_use).collect::<Vec<_>>().chunks(2) {
        let i: usize = *pair[0];
        let j: usize = *pair[1];

        let p1: Individual = population[i].clone();
        let p2: Individual = population[j].clone();

        let (c1, c2) = crossover(&p1, &p2);

        population[i] = c1;
        population[j] = c2;
    }
}

/* fn iterate_100_delete_incomplete(
    full_data: &mut Vec<(Vec<u16>, Vec<Individual>)>,
    testcases: &Vec<Testcase>,
) {
    for (changes, population) in full_data {
        for i in 0..100 {
            population.retain(|a: &Individual| lines_covered(a.clone(), &changes));
            if population.len() == 100 {
                println!("{i}: {:?}", population[0].tests.len());
                break;
            }
            population.sort();
            population.truncate(population.len() / 10);
            init_population(population, &testcases);
        }
    }
} */

fn evolve_one_generation(
    population: &mut Vec<Individual>,
    changes: &Vec<u16>,
    testcases: &Vec<Testcase>,
) {
    population.retain(|ind| lines_covered(ind.clone(), changes));
    if population.is_empty() {
        init_population(population, testcases);
        return;
    }

    population.sort();

    let len = population.len();
    let keep = ((len as f32) * 0.5).ceil() as usize;
    population.truncate(keep);

    crossover_x_percent_with_elite(population, 0.2, 0.1);
    //mutate_x_percent(population, testcases, 0.2, 0.1);
    init_population(population, testcases);
}

fn init_full_data(
    populations: &Vec<Vec<Individual>>,
    changed_lines: &Vec<Vec<u16>>,
) -> Vec<(Vec<u16>, Vec<Individual>)> {
    let result = changed_lines
        .clone()
        .into_iter()
        .zip(populations.clone().into_iter())
        .collect::<Vec<(Vec<u16>, Vec<Individual>)>>();
    result
}

fn main() {
    let testcases: Vec<Testcase> = read_testcases("coveredLines.txt").unwrap();
    let changed_lines: Vec<Vec<u16>> = read_changed_lines("changedLines.txt").unwrap();

    let populations: Vec<Vec<Individual>> = (0..changed_lines.len())
        .map(|_| {
            let mut pop: Vec<Individual> = Vec::new();
            init_population(&mut pop, &testcases);
            pop
        })
        .collect::<Vec<Vec<Individual>>>();

    let full_data: Vec<(Vec<u16>, Vec<Individual>)> = init_full_data(&populations, &changed_lines);

    for (changes, mut individuals) in full_data {
        println!("===========================================================");
        for _ in 0..1000 {
            evolve_one_generation(&mut individuals, &changes, &testcases);
        }
        let best = &individuals[0];
        println!("Number of Tests in best Individual: {}", best.tests.len());

        println!(
            "Testnames: {}",
            best.tests.iter().map(|tc| tc.name.as_str()).join(", ")
        );

        /* ===========================================================
        Number of Tests in best Individual: 1
        Testnames: removeFirstLast
        ===========================================================
        Number of Tests in best Individual: 1
        Testnames: removeFirstLast
        ===========================================================
        Number of Tests in best Individual: 4
        Testnames: test_equals, test_get_int, test_remove, test_setsize
        ===========================================================
        Number of Tests in best Individual: 2
        Testnames: test_addAll, test_capacityIncrease
        ===========================================================
        Number of Tests in best Individual: 7
        Testnames: removeFirstLast, test_add, test_capacityIncrease, test_contains, test_equals, test_get_int, test_set
        ===========================================================
        Number of Tests in best Individual: 10
        Testnames: removeFirstLast, test_capacityIncrease, test_contains, test_equals, test_get_int, test_remove, test_remove_index, test_remove_index2, test_set, test_setsize */
    }
}
