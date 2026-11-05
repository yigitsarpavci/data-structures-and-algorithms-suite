# Functional Language Interpreter (Aura)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language: Python 3](https://img.shields.io/badge/Language-Python%203.8%2B-3776AB?logo=python&logoColor=white)](#key-features)
[![Architecture: AST Interpreter](https://img.shields.io/badge/Architecture-AST%20%7C%20Dual%20Scoping-brightgreen.svg)](#key-features)
[![Course: CMPE 260](https://img.shields.io/badge/Course-CMPE%20260%20Boğaziçi-purple.svg)](#functional-language-interpreter-aura)

**Aura** is a lightweight, zero-dependency interpreter for a custom-designed functional programming language, built as part of the **CMPE 260 (Principles of Programming Languages)** curriculum at **Boğaziçi University**.

---

## Key Features

- **First-Class Functions & Closures:** Native support for anonymous lambdas, lexical closures, and higher-order function composition.
- **Rich Type System:** Supports arbitrary-precision integers, booleans, strings, and nested dynamic lists.
- **Dual Scoping Engine:** Toggleable runtime environment resolution between lexical (`--scope static`) and dynamic (`--scope dynamic`) scoping.
- **Hardened Runtime Diagnostics:** Strict type checking, line/column coordinate error reporting, and recursion depth guards.
- **Pure Python Core:** Zero external library dependencies.

---

## Usage

```bash
python3 interpreter.py examples/recursion.txt --scope static
```

### Available Flags

| Flag | Description |
| :--- | :--- |
| `--scope static` | Use lexical (static) scoping (default) |
| `--scope dynamic` | Use dynamic environment scoping |

---

## Project Structure

```text
04-functional-language-interpreter/
├── interpreter.py        # Lexer, recursive descent AST parser & evaluator
├── LICENSE               # MIT License
├── README.md             # Language specification and usage guide
├── examples/             # Sample Aura programs
│   ├── basics.txt
│   ├── closures.txt
│   ├── higher_order.txt
│   ├── recursion.txt
│   └── scope.txt
├── tests/                # Automated test suite with expected outputs
├── run_tests.sh          # Automated test runner script
└── docs/                 # Language design documentation
    ├── ast.pdf           # Abstract Syntax Tree specification
    ├── grammar.pdf       # Formal EBNF grammar specification
    └── report.pdf        # Technical design and evaluation report
```

---

## Local Setup & Testing

```bash
# Clone the suite repository
git clone https://github.com/yigitsarpavci/data-structures-and-algorithms-suite.git
cd data-structures-and-algorithms-suite/04-functional-language-interpreter

# Run an example program
python3 interpreter.py examples/basics.txt

# Execute the full automated test suite
chmod +x run_tests.sh
./run_tests.sh
```

---

## Documentation

Formal EBNF grammar specifications, AST diagrams, and the technical report are available in the [`docs/`](docs/) directory:
- [EBNF Grammar Specification (`docs/grammar.pdf`)](docs/grammar.pdf)
- [AST Structure Diagrams (`docs/ast.pdf`)](docs/ast.pdf)
- [Technical Design Report (`docs/report.pdf`)](docs/report.pdf)

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## Related Projects

- **[MatrixNet](../01-graph-network-analyzer)**: Linear-time graph network topology and Tarjan SCC connectivity analyzer.
- **[GigMatch-Pro](../02-marketplace-matching-simulation)**: Dynamic freelance marketplace allocator powered by indexed binary max-heaps.
- **[Nightpass](../03-state-machine-card-game)**: Deterministic finite state machine (FSM) survival card game engine.

---

*Developed by Yiğit Sarp Avcı*
