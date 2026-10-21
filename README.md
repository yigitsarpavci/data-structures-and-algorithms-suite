# Advanced Data Structures & Algorithms Suite

![Java](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)
![Algorithms](https://img.shields.io/badge/Algorithms-Tarjan%20%7C%20Max--Heap%20%7C%20FSM-007396)
![Complexity](https://img.shields.io/badge/Complexity-O(V%2BE)%20%7C%20O(log%20N)-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

A curated suite of core computer science implementations in **Java**, focusing on advanced graph topology decomposition, dynamic priority allocation engines, and deterministic finite state machines engineered for algorithmic fairness and memory efficiency.

---

## Suite Overview

| Module | Core Problem Domain | Key Algorithm / Data Structure | Time / Space Complexity |
|---|---|---|---|
| **[01. Graph Network Analyzer](./01-graph-network-analyzer)** | Fault tolerance & single-point-of-failure detection in communication topologies | **Tarjan's Strongly Connected Components (SCC)**, DFS Articulation Points | **$O(V + E)$** Time · $O(V)$ Space |
| **[02. Dynamic Marketplace Allocator](./02-marketplace-matching-simulation)** | High-throughput freelance order matching under multi-factor constraints | **Custom Binary Max-Heap**, Open-Addressing Hash Tables, Top-$K$ Queries | **$O(\log N)$** Insertion/Extraction |
| **[03. State-Machine Survival Game](./03-state-machine-card-game)** | Turn-based event simulation and resource balancing | **Deterministic Finite State Machine (FSM)**, Sequential Event Queues | Deterministic Linear Simulation |

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

---

## Build & Execution

All modules compile using standard Java (JDK 17+):

```bash
# Clone the repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite

# 1. Compile & run Graph Network Analyzer
cd 01-graph-network-analyzer
javac *.java
java Main input.txt output.txt

# 2. Compile & run Marketplace Matching
cd ../02-marketplace-matching-simulation
javac *.java
java GigMatch input.txt output.txt

# 3. Compile & run State-Machine Game
cd ../03-state-machine-card-game
javac *.java
java Nightpass input.txt output.txt
```

---

## Author

**Yiğit Sarp Avcı**  
Computer Engineering Student @ Boğaziçi University  
[GitHub Profile](https://github.com/yigitsarpavci) · [LinkedIn](https://www.linkedin.com/in/yigitsarpavci)
