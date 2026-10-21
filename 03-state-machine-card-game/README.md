# State-Machine Card Game (Nightpass)
## Table of Contents
- [Overview](#overview)
- [Gameplay Mechanics](#gameplay-mechanics)
- [Technical Implementation](#technical-implementation)
- [Installation](#installation)
- [Usage](#usage)
- [Testing](#testing)
- [License](#license)

---

## Overview

**Nightpass** is a strategy-based survival card game developed for the **CMPE 250 (Data Structures and Algorithms)** course at **Boğaziçi University**. The game challenges players to manage resources, navigate a deck of events, and survive the night using optimized decision-making algorithms and efficient data handling.

## Gameplay Mechanics

- **Resource Management**: Balance health, stamina, and items to survive.
- **Event Deck**: A randomized series of challenges, encounters, and rewards.
- **Strategic Combat**: Turn-based card mechanics requiring careful planning.
- **Survival Scaling**: Difficulty increases as the "night" progresses, testing the player's long-term strategy.

## Technical Implementation

The game's engine focuses on algorithmic fairness and state management:
- **State Machine**: A robust Java-based state machine handles game transitions and player actions.
- **Sequence Handling**: Uses advanced list structures and queues to manage the event deck and discard piles.
- **Balancing**: Implements mathematical models to ensure survival is challenging yet possible with the right strategy.
- **Memory Efficiency**: Optimized object instantiation to ensure smooth performance during long play sessions.

## Installation

1. Ensure you have **Java JDK 17+** installed.
2. Clone the repository:
   ```bash
   git clone https://github.com/yigitsarpavci/state-machine-card-game.git
   ```
3. Navigate to the project directory:
   ```bash
   cd state-machine-card-game
   ```

## Usage

Compile the source:
```bash
javac src/*.java -d bin/
```
Start the game:
```bash
java -cp bin Main
```

## Testing

Automated test cases are available to verify game logic and edge cases:
- Test scripts and data are located in the `tests/` directory.
- Detailed technical specifications can be found in `docs/Project2_description_v5.pdf`.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

### Related Projects
- **[Spireslayer](https://github.com/yigitsarpavci/unix-command-interpreter)**: C-based Systems Programming and CLI Design.
- **[MatrixNet](https://github.com/yigitsarpavci/graph-network-analyzer)**: Graph Theory and SCC Analysis.
- **[GigMatch-Pro](https://github.com/yigitsarpavci/marketplace-matching-simulation)**: Priority Queue Matching Algorithms.

---
*Developed by Yigit Sarp Avci*
