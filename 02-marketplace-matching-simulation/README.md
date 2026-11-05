# Marketplace Matching Simulation (GigMatch-Pro)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Java 17](https://img.shields.io/badge/Language-Java%2017-ED8B00?logo=openjdk&logoColor=white)](#tech-stack)
[![Data Structures: Indexed Max-Heap](https://img.shields.io/badge/Data%20Structures-Indexed%20Max--Heap%20%7C%20O(log%20N)-brightgreen.svg)](#tech-stack)
[![Course: CMPE 250](https://img.shields.io/badge/Course-CMPE%20250%20Boğaziçi-purple.svg)](#marketplace-matching-simulation-gigmatch-pro)

A high-throughput freelance marketplace simulation and priority matching engine in **Java 17**, engineered to manage real-time interactions between customers and service providers. Developed for the **CMPE 250 (Data Structures and Algorithms)** curriculum at **Boğaziçi University**.

The platform models a multi-tier service marketplace where freelancers are ranked and matched dynamically using a composite scoring function incorporating skill profiles, historical ratings, cancellation reliability, and burnout penalties.

---

## Key Features

### 1. User & Access Management
- Registration of customers and freelancers with O(1) unique ID enforcement via custom open-addressing hash tables.
- Customer-specific freelancer blacklisting and platform-wide ban enforcement.

### 2. Priority-Based Matching Engine
- Freelancers partitioned into specialized service buckets (`ServiceBucket`).
- Real-time top-$K$ candidate extraction using a custom **indexed binary max-heap** ($O(\log N)$ updates).
- Composite score evaluation combining weighted service skill vectors, incremental rating averages, reliability ratios, and burnout state.

### 3. Freelancer Lifecycle & Customer Loyalty
- Skill progression on high-rated completions and skill degradation on cancellations.
- Monthly simulation ticks updating burnout thresholds, queued price/service transitions, and customer loyalty tiers (`Bronze`, `Silver`, `Gold`, `Platinum`).

---

## Tech Stack

- **Language:** Java 17
- **Custom Data Structures (Zero `java.util` Collection Overhead):**
  - Open-addressing hash maps (`StringMap`) for $O(1)$ entity lookups
  - Dynamic resizing arrays (`ObjList`)
  - Indexed binary max-heaps (`ServiceBucket`) supporting $O(\log N)$ in-place priority key updates
- **I/O Optimization:** Custom buffered byte-stream parser (`FastScanner`) for high-volume simulation logs.

---

## Build and Run

```bash
# Clone the suite repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite/02-marketplace-matching-simulation

# Compile and run
javac src/*.java -d bin
java -cp bin Main input.txt output.txt
```

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Related Projects

- **[MatrixNet](../01-graph-network-analyzer)**: Linear-time graph network topology and Tarjan SCC connectivity analyzer.
- **[Nightpass](../03-state-machine-card-game)**: Deterministic finite state machine (FSM) survival card game engine.
- **[Aura](../04-functional-language-interpreter)**: Custom functional programming language interpreter with dual lexical and dynamic scoping.

---

*Developed by Yiğit Sarp Avcı*
