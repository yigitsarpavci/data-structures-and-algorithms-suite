# Graph Network Analyzer (MatrixNet)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Java 17](https://img.shields.io/badge/Language-Java%2017-ED8B00?logo=openjdk&logoColor=white)](#technical-specifications)
[![Algorithm: Tarjan SCC](https://img.shields.io/badge/Algorithm-Tarjan%20SCC%20%7C%20O(V%2BE)-brightgreen.svg)](#algorithmic-focus)
[![Course: CMPE 250](https://img.shields.io/badge/Course-CMPE%20250%20Boğaziçi-purple.svg)](#overview)

## Table of Contents
- [Overview](#overview)
- [Algorithmic Focus](#algorithmic-focus)
- [Key Features](#key-features)
- [Technical Specifications](#technical-specifications)
- [Installation & Usage](#installation--usage)
- [Testing](#testing)
- [License](#license)
- [Related Projects](#related-projects)

---

## Overview

**MatrixNet** is a graph-theory-based network topology analysis engine developed for the **CMPE 250 (Data Structures and Algorithms)** curriculum at **Boğaziçi University**. It simulates a mission-critical communication network where operators must identify single points of failure, isolate disconnected clusters, and optimize routing across dynamic graph topologies.

---

## Algorithmic Focus

The core of MatrixNet is built on linear-time graph connectivity and pathfinding analysis:
- **Strongly Connected Components (SCC):** Implements **Tarjan's Algorithm** via DFS low-link discovery to identify isolated or mutually reachable network clusters.
- **Articulation Points:** Detects critical bridge nodes whose failure would fragment the underlying communication network.
- **Complexity Guarantee:** Engineered to process large-scale graphs with $V$ vertices and $E$ edges in optimal $O(V + E)$ linear time.

---

## Key Features

- **Real-Time Topology Analysis:** Efficiently parses and evaluates dynamic network mutations.
- **Connectivity Mapping:** Identifies structural bottlenecks and isolated subgraphs.
- **Zero-Overhead Data Structures:** Uses custom adjacency lists, explicit traversal stacks, and bitsets to eliminate Java collection boxing and garbage collection pressure.
- **Automated Validation:** Verified against an automated Python test runner (`tests/test_runner.py`).

---

## Technical Specifications

- **Language:** Java 17
- **Data Structures:** Custom Adjacency Lists, Explicit DFS Stacks, and Visited Bitsets.
- **Memory Optimization:** Reuses pre-allocated state arrays across queries to avoid heap fragmentation.

---

## Installation & Usage

```bash
# Clone the suite repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite/01-graph-network-analyzer

# Compile the source
javac src/*.java -d bin

# Run the network analyzer
java -cp bin Main input.txt output.txt
```

---

## Testing

Run the automated test suite against all reference input/output pairs:

```bash
python3 tests/test_runner.py
```

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Related Projects

- **[GigMatch-Pro](../02-marketplace-matching-simulation)**: Dynamic freelance marketplace allocator powered by indexed binary max-heaps and open-addressing hash tables.
- **[Nightpass](../03-state-machine-card-game)**: Deterministic finite state machine (FSM) survival card game engine.
- **[Aura](../04-functional-language-interpreter)**: Custom functional programming language interpreter with dual lexical and dynamic scoping.

---

*Developed by Yiğit Sarp Avcı*
