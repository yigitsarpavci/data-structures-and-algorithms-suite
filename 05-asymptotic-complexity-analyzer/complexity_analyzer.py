"""
Asymptotic & Probabilistic Complexity Analyzer
Course: CMPE 300 - Analysis of Algorithms (Boğaziçi University)
Author: Yiğit Sarp Avcı

Implementation of a multi-branch string-processing algorithm, basic-operation
counter verification across 4 operation sets, closed-form theoretical bounds
(B_k(n), W_k(n), A_k(n)), exhaustive 3^n state-space enumeration, and empirical
execution-time benchmarking.

Usage:
    python3 complexity_analyzer.py                        # timing experiment (Table 2, default Variant 2)
    python3 complexity_analyzer.py --count <string>       # count mode (e.g. aabcbba)
    python3 complexity_analyzer.py --verify               # exact count verification (Tables 3, 4)
    python3 complexity_analyzer.py --exhaustive           # exhaustive 3^n verification (n = 1..9)
    python3 complexity_analyzer.py <No1> <No2> [--count <str> | --verify]
"""

import itertools
import random
import sys
import time
from fractions import Fraction

# Variant table from Section 2.1 of the project specification:
# key = (No1 + No2) mod 8 -> (p_a, p_b, p_c, d)
VARIANTS = {
    0: (Fraction(1, 2), Fraction(1, 4), Fraction(1, 4), 2),
    1: (Fraction(1, 2), Fraction(1, 4), Fraction(1, 4), 3),
    2: (Fraction(1, 3), Fraction(1, 3), Fraction(1, 3), 2),
    3: (Fraction(1, 3), Fraction(1, 3), Fraction(1, 3), 3),
    4: (Fraction(2, 5), Fraction(2, 5), Fraction(1, 5), 2),
    5: (Fraction(2, 5), Fraction(2, 5), Fraction(1, 5), 3),
    6: (Fraction(1, 6), Fraction(1, 2), Fraction(1, 3), 2),
    7: (Fraction(1, 6), Fraction(1, 2), Fraction(1, 3), 3),
}

# Experiment parameters required by Sections 4 and 5
TIMING_SIZES = [1, 5, 10, 20, 40, 50, 60, 80, 100, 150, 200, 250, 300, 350, 400]
AVERAGE_RUNS = 10
VERIFY_SIZES_BW = [1, 5, 10, 20, 40, 100]
VERIFY_SIZES_AVG = [10, 40, 100]
VERIFY_RANDOM_INPUTS = 2000


# ---------------------------------------------------------------------------
# Algorithm implementations (plain for timing, instrumented for counting)
# ---------------------------------------------------------------------------
def algorithm(arr, d):
    """Execute Algorithm 1 without operation counters.

    Used exclusively during execution-time measurements (Section 4) so that
    counter updates do not introduce measurement overhead.
    """
    n = len(arr)
    res = 0                                      # line 1: res <- 0
    s = 0                                        # line 1: s <- 0
    for i in range(n):                           # line 2: for i <- 0 to n - 1
        if arr[i] == 'a':                        # line 3: if arr[i] = 'a'
            s = s + 1                            # line 4: s <- s + 1
            k = n                                # line 5: k <- n
            while k > 0:                         # line 6: while k > 0
                k = k // d                       # line 7: k <- floor(k / d)
        elif arr[i] == 'b':                      # line 8: else if arr[i] = 'b'
            for q in range(0, s + 1):            # line 9: for q <- 0 to s
                res = res + q                    # line 10: res <- res + q
                for r in range(1, n + 1):        # line 11: for r <- 1 to n
                    res = res + r                # line 12: res <- res + r
        else:                                    # line 13: else (arr[i] = 'c')
            s = 0                                # line 14: s <- 0
            for t in range(i, n):                # line 15: for t <- i to n - 1
                res = res + t                    # line 16: res <- res + t
    return res                                   # line 17: return res


def algorithm_counted(arr, d):
    """Execute Algorithm 1 while counting each basic-operation set (1)-(4).

    Basic-operation sets defined in Section 3:
        (1) Comparisons on lines 3 and 8.
        (2) Loop increments of the for-loops on lines 9 and 15 (one per iteration).
        (3) Assignments on lines 4, 10, and 14.
        (4) Assignments on lines 7, 12, and 16.
    """
    n = len(arr)
    c1 = c2 = c3 = c4 = 0
    res = 0
    s = 0
    for i in range(n):
        c1 += 1                                  # (1) comparison on line 3
        if arr[i] == 'a':
            s = s + 1
            c3 += 1                              # (3) assignment on line 4
            k = n
            while k > 0:
                k = k // d
                c4 += 1                          # (4) assignment on line 7
        else:
            c1 += 1                              # (1) comparison on line 8 (evaluated when arr[i] != 'a')
            if arr[i] == 'b':
                for q in range(0, s + 1):
                    c2 += 1                      # (2) loop increment on line 9
                    res = res + q
                    c3 += 1                      # (3) assignment on line 10
                    for r in range(1, n + 1):
                        res = res + r
                        c4 += 1                  # (4) assignment on line 12
            else:
                s = 0
                c3 += 1                          # (3) assignment on line 14
                for t in range(i, n):
                    c2 += 1                      # (2) loop increment on line 15
                    res = res + t
                    c4 += 1                      # (4) assignment on line 16
    return res, (c1, c2, c3, c4)


# ---------------------------------------------------------------------------
# Input generators and exact closed-form formulas (Section 3)
# ---------------------------------------------------------------------------
def L(n, d):
    """Return the exact iteration count of the while loop on lines 6-7: floor(log_d(n)) + 1.

    Uses exact integer exponentiation rather than floating-point logarithms.
    """
    m = 0
    while d ** (m + 1) <= n:
        m += 1
    return m + 1


def best_input(case, n, d):
    """Construct a best-case input of size n for basic-operation set `case` (1..4)."""
    if case in (1, 2, 3):
        return 'a' * n
    # Case 4: 'a' at indices 0..n-L where L <= n-i, and 'c' at the last L-1 indices
    loop_steps = L(n, d)
    return 'a' * (n - loop_steps + 1) + 'c' * (loop_steps - 1)


def worst_input(case, n, d):
    """Construct a worst-case input of size n for basic-operation set `case` (1..4)."""
    if case in (1, 2):
        return 'c' * n
    # Cases 3 and 4: floor(n/2) 'a's followed by ceil(n/2) 'b's to maximise s at each 'b'
    half = n // 2
    return 'a' * half + 'b' * (n - half)


def random_input(n, pa, pb):
    """Generate a random input of length n with P('a')=pa, P('b')=pb, P('c')=1-pa-pb."""
    out = []
    for _ in range(n):
        u = random.random()
        out.append('a' if u < pa else ('b' if u < pa + pb else 'c'))
    return ''.join(out)


def expected_s_sum(n, pa, pc):
    """Compute sum_{i=0}^{n-1} E[s_i] in closed form, where E[s_i] = (pa/pc)*(1 - (1-pc)^i)."""
    return (pa / pc) * (n - (1 - (1 - pc) ** n) / pc)


def formulas(n, variant):
    """Evaluate the exact closed-form expressions B_k(n), W_k(n), A_k(n) for k = 1..4."""
    pa, pb, pc, d = variant
    loop_steps = L(n, d)
    half = n // 2
    s_sum = expected_s_sum(n, pa, pc)

    B = (
        n,
        0,
        n,
        (n - loop_steps + 1) * loop_steps + (loop_steps - 1) * loop_steps // 2,
    )
    W = (
        2 * n,
        n * (n + 1) // 2,
        half + (n - half) * (half + 1),
        half * loop_steps + n * (n - half) * (half + 1),
    )
    A = (
        n * (2 - pa),
        pb * (n + s_sum) + pc * Fraction(n * (n + 1), 2),
        pa * n + pc * n + pb * (n + s_sum),
        pa * n * loop_steps + pb * n * (n + s_sum) + pc * Fraction(n * (n + 1), 2),
    )
    return B, W, A


# ---------------------------------------------------------------------------
# Execution modes (Section 7)
# ---------------------------------------------------------------------------
def variant_line(key, variant):
    """Format the required first output line describing the active variant."""
    pa, pb, pc, d = variant
    return f"Variant: {key} (p_a={pa}, p_b={pb}, p_c={pc}, d={d})"


def time_one(arr, d):
    """Measure the elapsed time (in seconds) of a single counter-free execution."""
    start = time.perf_counter()
    algorithm(arr, d)
    return time.perf_counter() - start


def timing_mode(variant):
    """Run the real-execution benchmark for all 15 sizes and 3 input types (Section 4)."""
    pa, pb, _, d = variant
    for case in ("best", "worst", "average"):
        for n in TIMING_SIZES:
            if case == "best":
                elapsed = time_one(best_input(4, n, d), d)
                print(f"Case: best Size: {n} Elapsed Time (s): {elapsed:.9f}")
            elif case == "worst":
                elapsed = time_one(worst_input(4, n, d), d)
                print(f"Case: worst Size: {n} Elapsed Time (s): {elapsed:.9f}")
            else:
                runs = [time_one(random_input(n, pa, pb), d) for _ in range(AVERAGE_RUNS)]
                avg_elapsed = sum(runs) / len(runs)
                print(f"Case: average Size: {n} Elapsed Time (s): {avg_elapsed:.9f}")
                print("    Runs: " + " ".join(f"{t:.9f}" for t in runs))


def count_mode(string, variant):
    """Run the algorithm once on `string` and print the four operation counts (Section 7)."""
    _, counts = algorithm_counted(string, variant[3])
    print("Count(1): {} Count(2): {} Count(3): {} Count(4): {}".format(*counts))


def verify_mode(variant):
    """Compare empirical operation counts against closed-form formulas (Section 5)."""
    pa, pb, _, d = variant
    print("Exact count verification (best / worst case)")
    print(f"{'case':>4} {'n':>4} {'counted B':>12} {'formula B':>12} {'counted W':>12} {'formula W':>12}")
    for case in (1, 2, 3, 4):
        for n in VERIFY_SIZES_BW:
            B, W, _ = formulas(n, variant)
            cb = algorithm_counted(best_input(case, n, d), d)[1][case - 1]
            cw = algorithm_counted(worst_input(case, n, d), d)[1][case - 1]
            print(f"{case:>4} {n:>4} {cb:>12} {B[case - 1]:>12} {cw:>12} {W[case - 1]:>12}")

    print()
    print(f"Exact count verification (average case, {VERIFY_RANDOM_INPUTS} random inputs per n)")
    print(f"{'case':>4} {'n':>4} {'mean count':>14} {'formula A':>14} {'difference':>12}")
    random.seed(300)
    for n in VERIFY_SIZES_AVG:
        totals = [0, 0, 0, 0]
        for _ in range(VERIFY_RANDOM_INPUTS):
            counts = algorithm_counted(random_input(n, pa, pb), d)[1]
            for j in range(4):
                totals[j] += counts[j]
        _, _, A = formulas(n, variant)
        for case in (1, 2, 3, 4):
            mean = totals[case - 1] / VERIFY_RANDOM_INPUTS
            exact_avg = float(A[case - 1])
            print(f"{case:>4} {n:>4} {mean:>14.3f} {exact_avg:>14.3f} {mean - exact_avg:>12.3f}")


def exhaustive_check(variant, max_n=9):
    """Verify B_k(n), W_k(n), and A_k(n) by enumerating all 3^n inputs for 1 <= n <= max_n."""
    pa, pb, pc, d = variant
    probs = {'a': pa, 'b': pb, 'c': pc}
    for n in range(1, max_n + 1):
        B, W, A = formulas(n, variant)
        mins = [10 ** 18] * 4
        maxs = [0] * 4
        expected = [Fraction(0)] * 4
        for tup in itertools.product('abc', repeat=n):
            counts = algorithm_counted(tup, d)[1]
            weight = Fraction(1)
            for ch in tup:
                weight *= probs[ch]
            for j in range(4):
                mins[j] = min(mins[j], counts[j])
                maxs[j] = max(maxs[j], counts[j])
                expected[j] += weight * counts[j]
        for j in range(4):
            cb = algorithm_counted(best_input(j + 1, n, d), d)[1][j]
            cw = algorithm_counted(worst_input(j + 1, n, d), d)[1][j]
            if not (mins[j] == B[j] == cb and maxs[j] == W[j] == cw and expected[j] == A[j]):
                raise AssertionError(f"Mismatch at n={n}, case={j + 1}")
    return True


def main():
    """Parse CLI arguments and dispatch to the requested execution mode."""
    args = sys.argv[1:]
    if len(args) >= 2 and args[0].isdigit() and args[1].isdigit():
        key = (int(args[0]) + int(args[1])) % 8
        rest = args[2:]
    else:
        key = 2
        rest = args

    variant = VARIANTS[key]
    print(variant_line(key, variant))

    if len(rest) >= 1 and rest[0] == "--count":
        input_str = rest[1] if len(rest) >= 2 else ""
        count_mode(input_str, variant)
    elif len(rest) >= 1 and rest[0] == "--verify":
        verify_mode(variant)
    elif len(rest) >= 1 and rest[0] == "--exhaustive":
        if exhaustive_check(variant, max_n=9):
            print("All closed-form formulas B_k(n), W_k(n), A_k(n) verified exhaustively for n = 1..9.")
    else:
        timing_mode(variant)


if __name__ == "__main__":
    main()
