# Asymptotic & Probabilistic Complexity Analyzer

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Python 3](https://img.shields.io/badge/Language-Python%203.8%2B-3776AB?logo=python&logoColor=white)](#installation--usage)
[![Complexity: Θ(n log n) | Θ(n²) | Θ(n³)](https://img.shields.io/badge/Complexity-%CE%98(n%20log%20n)%20%7C%20%CE%98(n%C2%B2)%20%7C%20%CE%98(n%C2%B3)-brightgreen.svg)](#closed-form-bounds--asymptotic-complexity)
[![Course: CMPE 300](https://img.shields.io/badge/Course-CMPE%20300%20Boğaziçi-purple.svg)](#overview)

## Table of Contents
- [Overview](#overview)
- [Algorithm & Basic-Operation Sets](#algorithm--basic-operation-sets)
- [Closed-Form Bounds & Asymptotic Complexity](#closed-form-bounds--asymptotic-complexity)
- [Empirical Verification & Benchmarking](#empirical-verification--benchmarking)
- [Project Structure](#project-structure)
- [Installation & Usage](#installation--usage)
- [Full Technical Report](#full-technical-report)
- [License](#license)
- [Related Projects](#related-projects)

---

## Overview

An exact operation-counting, probabilistic expectation modeling, and empirical benchmarking suite developed for the **CMPE 300 (Analysis of Algorithms)** curriculum at **Boğaziçi University**.

The module analyzes a three-branch string-processing algorithm over $\Sigma = \{\text{`a'}, \text{`b'}, \text{`c'}\}$ whose inner-loop bounds depend dynamically on prefix state $s_i$ (the run of `'a'` characters since the most recent `'c'`). It derives exact non-asymptotic closed-form expressions for the **best-case** $B_k(n)$, **worst-case** $W_k(n)$, and **average-case** $A_k(n)$ across four distinct basic-operation sets and verifies them through exhaustive state-space enumeration ($3^n$), Monte Carlo sampling, and wall-clock runtime profiling.

---

## Algorithm & Basic-Operation Sets

Given an input string `arr[0..n-1]` over $\{\text{`a'}, \text{`b'}, \text{`c'}\}$ and division factor $d = 2$ (with $L(n) = \lfloor \log_2 n \rfloor + 1$):
- **`arr[i] == 'a'`:** Increments state counter $s \leftarrow s + 1$ and executes a logarithmic division loop ($k \leftarrow \lfloor k / d \rfloor$, $L(n)$ iterations).
- **`arr[i] == 'b'`:** Leaves $s$ unchanged and runs a nested loop of $(s + 1) \times n$ inner iterations.
- **`arr[i] == 'c'`:** Resets $s \leftarrow 0$ and runs a suffix loop of $n - i$ iterations.

Operation counts are tracked across four basic-operation sets:
1. **Set (1) — Branch Comparisons:** Conditional checks `arr[i] == 'a'` and `arr[i] == 'b'`.
2. **Set (2) — For-Loop Increments:** Outer loop increments for the `'b'` ($s_i + 1$) and `'c'` ($n - i$) branches.
3. **Set (3) — State & Outer Assignments:** Assignments $s \leftarrow s + 1$, $\text{res} \leftarrow \text{res} + q$, and $s \leftarrow 0$.
4. **Set (4) — Inner-Loop Assignments:** Dominant inner-loop assignments across all three branches ($L(n)$, $(s_i + 1)n$, and $n - i$).

---

## Closed-Form Bounds & Asymptotic Complexity

Under Variant 2 ($p_a = p_b = p_c = \frac{1}{3}$, $d = 2$), indicator random variables yield the expected prefix state $E[s_i] = 1 - \left(\frac{2}{3}\right)^i$ and cumulative expectation $S(n) = \sum_{i=0}^{n-1} E[s_i] = n - 3\left(1 - \left(\frac{2}{3}\right)^n\right)$, with $h = \lfloor n / 2 \rfloor$:

| Set | Best Case $B_k(n)$ | Worst Case $W_k(n)$ | Average Case $A_k(n)$ | Asymptotic $(B, W, A)$ |
| :---: | :--- | :--- | :--- | :---: |
| **(1)** | $n$ | $2n$ | $\frac{5}{3}n$ | $\Theta(n),\ \Theta(n),\ \Theta(n)$ |
| **(2)** | $0$ | $\frac{n(n+1)}{2}$ | $\frac{n^2 + 5n}{6} - 1 + \left(\frac{2}{3}\right)^n$ | $\Theta(1),\ \Theta(n^2),\ \Theta(n^2)$ |
| **(3)** | $n$ | $h + (n - h)(h + 1)$ | $\frac{4}{3}n - 1 + \left(\frac{2}{3}\right)^n$ | $\Theta(n),\ \Theta(n^2),\ \Theta(n)$ |
| **(4)** | $(n - L + 1)L + \frac{L(L - 1)}{2}$ | $hL + n(n - h)(h + 1)$ | $\frac{n L}{3} + \frac{5n^2 + n}{6} - n\left(1 - \left(\frac{2}{3}\right)^n\right)$ | $\Theta(n \log n),\ \Theta(n^3),\ \Theta(n^2)$ |

---

## Empirical Verification & Benchmarking

- **Exhaustive $3^n$ Verification (`--exhaustive`):** Enumerates all $3^n$ input strings for $1 \le n \le 9$ using exact rational arithmetic (`fractions.Fraction`), confirming that the empirical minimum, maximum, and probability-weighted mean match $B_k(n)$, $W_k(n)$, and $A_k(n)$ identically.
- **Instrumental Counter & Monte Carlo Verification (`--verify`):** Validates best/worst-case inputs for $n \in \{1, 5, 10, 20, 40, 100\}$ with zero error and compares 2,000-trial random averages against $A_k(n)$ for $n \in \{10, 40, 100\}$.
- **Counter-Free Runtime Benchmarking:** Measures wall-clock execution across 15 input sizes ($n \in [1, 400]$), demonstrating empirical convergence to $\Theta(n \log n)$ best-case, $\Theta(n^2)$ average-case, and $\Theta(n^3)$ worst-case scaling.

---

## Project Structure

```text
05-asymptotic-complexity-analyzer/
├── complexity_analyzer.py       # Algorithm, closed-form formulas, counters & benchmark CLI
├── LICENSE                      # MIT License
├── README.md                    # Documentation and complexity summary
├── tools/
│   ├── brute_force_check.py     # Exhaustive 3^n exact rational verification (n = 1..9)
│   └── make_report_assets.py    # Matplotlib figure generator for theoretical vs. empirical plots
└── report/
    ├── report.pdf               # 8-page technical report with formal proofs and tables
    ├── report.tex               # LaTeX source of the technical report
    └── figures/                 # Generated vector plots (case4.pdf, times.pdf, ratio.pdf, theory4.pdf)
```

---

## Installation & Usage

Requires **Python 3.8+** (standard library only; zero external runtime dependencies).

```bash
# Clone the suite repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite/05-asymptotic-complexity-analyzer

# 1. Run real-execution timing benchmark across 15 input sizes (n = 1..400)
python3 complexity_analyzer.py

# 2. Count exact basic operations (1)-(4) on a specific string
python3 complexity_analyzer.py --count aabcbba

# 3. Verify closed-form formulas B_k(n), W_k(n), and A_k(n)
python3 complexity_analyzer.py --verify

# 4. Run exhaustive 3^n state-space check for n = 1..9
python3 complexity_analyzer.py --exhaustive
```

---

## Full Technical Report

The complete 8-page technical report—containing formal inductive/monotonicity proofs, exact closed-form derivations, operation-count verification tables, and log-log scaling plots—is available at [`report/report.pdf`](report/report.pdf).

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Related Projects

- **[MatrixNet](../01-graph-network-analyzer)**: Linear-time graph network topology and Tarjan SCC connectivity analyzer.
- **[GigMatch-Pro](../02-marketplace-matching-simulation)**: Dynamic freelance marketplace allocator powered by indexed binary max-heaps.
- **[Nightpass](../03-state-machine-card-game)**: Deterministic finite state machine (FSM) survival card game engine.
- **[Aura](../04-functional-language-interpreter)**: Custom functional programming language interpreter with dual lexical and dynamic scoping.

---

*Developed by Yiğit Sarp Avcı*
