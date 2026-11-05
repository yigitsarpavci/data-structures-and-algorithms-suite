# Advanced Data Structures & Algorithms Suite

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Java](https://img.shields.io/badge/Language-Java%2017-ED8B00?logo=openjdk&logoColor=white)](#suite-overview)
[![Language: Python](https://img.shields.io/badge/Language-Python%203-3776AB?logo=python&logoColor=white)](#4-functional-language-interpreter-04-functional-language-interpreter)
[![Algorithms](https://img.shields.io/badge/Algorithms-Tarjan%20%7C%20Max--Heap%20%7C%20FSM%20%7C%20AST-007396)](#module-details)
[![Complexity](https://img.shields.io/badge/Complexity-O(V%2BE)%20%7C%20O(log%20N)-brightgreen)](#suite-overview)

A curated suite of core computer science and computational theory implementations in **Java 17** and **Python 3**, focusing on graph topology decomposition, dynamic priority allocation engines, deterministic finite state machines, and recursive Abstract Syntax Tree (AST) evaluation.

---

## Suite Overview

| Module | Core Problem Domain | Key Algorithm / Data Structure | Time / Space Complexity |
|---|---|---|---|
| **[01. Graph Network Analyzer](./01-graph-network-analyzer)** | Fault tolerance & single-point-of-failure detection in communication topologies | **Tarjan's Strongly Connected Components (SCC)**, DFS Articulation Points | **$O(V + E)$** Time · $O(V)$ Space |
| **[02. Dynamic Marketplace Allocator](./02-marketplace-matching-simulation)** | High-throughput freelance order matching under multi-factor constraints | **Custom Binary Max-Heap**, Open-Addressing Hash Tables, Top-$K$ Queries | **$O(\log N)$** Insertion/Extraction |
| **[03. State-Machine Survival Game](./03-state-machine-card-game)** | Turn-based event simulation and resource balancing | **Deterministic Finite State Machine (FSM)**, Sequential Event Queues | Deterministic Linear Simulation |
| **[04. Functional Language Interpreter](./04-functional-language-interpreter)** | Custom functional language execution & scope evaluation | **AST Tree Traversal**, Dual Lexical/Dynamic Scoping, Closures | Recursive AST Evaluation |

---

## Module Details

### 1. Graph Network Analyzer (`01-graph-network-analyzer`)
- **Problem Statement:** Detect network partitions and vulnerable infrastructure routing bottlenecks without exhaustive path exploration.
- **Implementation:**
  - Identifies bridge nodes and isolated clusters using **Tarjan's SCC algorithm** via DFS low-link discovery.
  - Implements custom memory-optimized adjacency lists and visited bitsets, eliminating standard collection object overhead.
  - Includes automated test harness with recursive and iterative traversal benchmarks.

### 2. Marketplace Matching Simulation (`02-marketplace-matching-simulation`)
- **Problem Statement:** Dynamically match incoming service orders to optimal freelancers based on real-time composite score functions (skill compatibility, rating history, reliability, and burnout penalties).
- **Implementation:**
  - Built on a **custom binary max-heap** with indexed element tracking, guaranteeing $O(\log N)$ priority extractions and dynamic in-place key updates.
  - Custom open-addressing hash maps and dynamically resizing array structures for fast attribute lookups.
  - Encapsulates blacklist rules, service-tier categorization, and top-$K$ candidate queries.

### 3. State-Machine Card Game (`03-state-machine-card-game`)
- **Problem Statement:** Model a deterministic survival card game loop where player resource depletion and event difficulty scale harmoniously.
- **Implementation:**
  - Governed by a **deterministic Finite State Machine (FSM)** with cleanly decoupled state transitions (Draw, Encounter, Decision, Resolution, Turn End).
  - Queue-based event deck randomization, discard management, and player health/stamina balancing verified through automated difficulty modeling suites.

### 4. Functional Language Interpreter (`04-functional-language-interpreter`)
- **Problem Statement:** Build a hardened interpreter for a custom-designed functional programming language supporting first-class functions, closures, dynamic lists, and dual scoping mechanics.
- **Implementation:**
  - Implements recursive Abstract Syntax Tree (AST) evaluation with strict coordinate-based runtime error diagnostics.
  - Features toggleable lexical (static) and dynamic scoping resolution via isolated environment chains.
  - Includes recursion depth guards, comprehensive test suites, and zero external runtime dependencies.

---

## Build & Execution

Modules 01–03 compile using standard Java (JDK 17+); Module 04 runs on Python 3.8+:

```bash
# Clone the repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite

# 1. Compile & run Graph Network Analyzer
cd 01-graph-network-analyzer
javac src/*.java -d bin
java -cp bin Main input.txt output.txt

# 2. Compile & run Marketplace Matching Simulation
cd ../02-marketplace-matching-simulation
javac src/*.java -d bin
java -cp bin Main input.txt output.txt

# 3. Compile & run State-Machine Survival Game
cd ../03-state-machine-card-game
javac src/*.java -d bin
java -cp bin Main input.txt output.txt

# 4. Run Functional Language Interpreter
cd ../04-functional-language-interpreter
python3 interpreter.py examples/recursion.txt --scope static
```

---

## License

This repository is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Author

**Yiğit Sarp Avcı**  
Computer Engineering Student @ Boğaziçi University  
[GitHub Profile](https://github.com/yigitsarpavci) · [LinkedIn](https://www.linkedin.com/in/yigitsarpavci)
