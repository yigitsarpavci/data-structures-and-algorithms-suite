# State-Machine Card Game (Nightpass)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Java 17](https://img.shields.io/badge/Language-Java%2017-ED8B00?logo=openjdk&logoColor=white)](#technical-implementation)
[![Architecture: Deterministic FSM](https://img.shields.io/badge/Architecture-Deterministic%20FSM-brightgreen.svg)](#technical-implementation)
[![Course: CMPE 250](https://img.shields.io/badge/Course-CMPE%20250%20Boğaziçi-purple.svg)](#overview)

## Table of Contents
- [Overview](#overview)
- [Gameplay Mechanics](#gameplay-mechanics)
- [Technical Implementation](#technical-implementation)
- [Installation & Usage](#installation--usage)
- [Testing](#testing)
- [License](#license)
- [Related Projects](#related-projects)

---

## Overview

**Nightpass** is a deterministic, strategy-based survival card game engine developed for the **CMPE 250 (Data Structures and Algorithms)** curriculum at **Boğaziçi University**. The engine models resource-constrained decision-making across sequential card encounters using custom priority structures and state machines.

---

## Gameplay Mechanics

- **Resource Management:** Balances player health, stamina, and deck state across multi-round encounters.
- **Event Deck Queueing:** Sequential series of combat encounters, deck mutations, and state transitions.
- **Strategic Combat:** Turn-based card resolution governed by strict tie-breaking and priority rules.
- **Survival Scaling:** Encounter difficulty scales deterministically as rounds progress.

---

## Technical Implementation

- **Deterministic Finite State Machine (FSM):** Cleanly decouples draw, encounter, decision, resolution, and turn-end phases.
- **Custom Sequence & Priority Structures:** Manages active card pools and discard piles without standard library overhead.
- **Memory Efficiency:** Minimizes object allocation during high-frequency simulation loops.

---

## Installation & Usage

```bash
# Clone the suite repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite/03-state-machine-card-game

# Compile the source
javac src/*.java -d bin

# Run the simulation engine
java -cp bin Main input.txt output.txt
```

---

## Testing

Automated test cases (`Type1`, `Type2`, `Type3`) are located in the `tests/testcases_v4/` directory to verify state transitions and tie-breaking edge cases.

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Related Projects

- **[MatrixNet](../01-graph-network-analyzer)**: Linear-time graph network topology and Tarjan SCC connectivity analyzer.
- **[GigMatch-Pro](../02-marketplace-matching-simulation)**: Dynamic freelance marketplace allocator powered by indexed binary max-heaps.
- **[Aura](../04-functional-language-interpreter)**: Custom functional programming language interpreter with dual lexical and dynamic scoping.

---

*Developed by Yiğit Sarp Avcı*
