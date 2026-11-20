"""Generates the Section 6 comparison figures (case4.pdf, times.pdf, ratio.pdf,
theory4.pdf) in report/figures/ from the Table 2 timing records and exact formulas."""

import importlib.util
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FIG = os.path.join(ROOT, "report", "figures")
os.makedirs(FIG, exist_ok=True)

target = os.path.join(ROOT, "complexity_analyzer.py")
spec = importlib.util.spec_from_file_location("prog", target)
prog = importlib.util.module_from_spec(spec)
spec.loader.exec_module(prog)
VARIANT = prog.VARIANTS[2]

# Table 2 measured execution times (in seconds) for basic-operation set (4)
times = {
    "best": {
        1: 0.000001500, 5: 0.000002700, 10: 0.000002200, 20: 0.000003700,
        40: 0.000007900, 50: 0.000009500, 60: 0.000011300, 80: 0.000016200,
        100: 0.000020200, 150: 0.000032400, 200: 0.000043800, 250: 0.000054700,
        300: 0.000075300, 350: 0.000085400, 400: 0.000099600,
    },
    "worst": {
        1: 0.000001100, 5: 0.000003200, 10: 0.000013200, 20: 0.000075600,
        40: 0.000500000, 50: 0.001156000, 60: 0.001719200, 80: 0.003732400,
        100: 0.006754300, 150: 0.023097300, 200: 0.054622700, 250: 0.109360300,
        300: 0.204555200, 350: 0.347920700, 400: 0.536697400,
    },
    "average": {
        1: 0.000000630, 5: 0.000001720, 10: 0.000003650, 20: 0.000015040,
        40: 0.000044940, 50: 0.000063670, 60: 0.000099850, 80: 0.000151920,
        100: 0.000261490, 150: 0.000542950, 200: 0.000909290, 250: 0.001489160,
        300: 0.002306830, 350: 0.003400350, 400: 0.004937900,
    },
}
sizes = sorted(times["best"])

try:
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
except ImportError:
    print("matplotlib not installed; keeping existing figures in", FIG)
    raise SystemExit(0)

plt.rcParams.update({"font.size": 9, "axes.grid": True, "grid.alpha": 0.3})
dense = list(range(1, 401))
B4 = [float(prog.formulas(n, VARIANT)[0][3]) for n in dense]
W4 = [float(prog.formulas(n, VARIANT)[1][3]) for n in dense]
A4 = [float(prog.formulas(n, VARIANT)[2][3]) for n in dense]
ASYM4 = {"B": r"$\Theta(n\log n)$", "W": r"$\Theta(n^3)$", "A": r"$\Theta(n^2)$"}

# 1. Case 4 theoretical vs. measured execution times
fig, axes = plt.subplots(1, 3, figsize=(11, 3.3))
for ax, (lab, y_vals, tkey) in zip(axes, [("B", B4, "best"), ("W", W4, "worst"), ("A", A4, "average")]):
    t_vals = [times[tkey][n] for n in sizes]
    ax.plot(dense, y_vals, color="#1f5fa8", lw=1.6,
            label=f"${lab}_4(n)$ theoretical " + ASYM4[lab])
    ax.set_xlabel("input size $n$")
    ax.set_ylabel("basic operations", color="#1f5fa8")
    ax2 = ax.twinx()
    ax2.plot(sizes, t_vals, "o--", color="#c2410c", ms=3.5, lw=1,
             label=f"measured time ({tkey} input)")
    ax2.set_ylabel("time (s)", color="#c2410c")
    ax2.grid(False)

    y_top = max(y_vals)
    t_top = max(t_vals)
    ax.set_ylim(-0.04 * y_top, 1.22 * y_top)
    ax2.set_ylim(-0.04 * t_top, 1.22 * t_top)

    ax.set_title(f"${lab}_4(n)$ vs. {tkey}-case time")
    h1, l1 = ax.get_legend_handles_labels()
    h2, l2 = ax2.get_legend_handles_labels()
    ax.legend(h1 + h2, l1 + l2, loc="upper left", fontsize=7, framealpha=0.9)
fig.suptitle("Case 4: basic operations = assignments (4)", fontsize=10)
fig.tight_layout()
fig.savefig(os.path.join(FIG, "case4.pdf"))
plt.close(fig)

# 2. All 45 measured times, linear and log-log axes
fig, axes = plt.subplots(1, 2, figsize=(11, 3.6))
for ax, log in zip(axes, (False, True)):
    for key, col in (("best", "#15803d"), ("worst", "#b91c1c"), ("average", "#1d4ed8")):
        ax.plot(sizes, [times[key][n] for n in sizes], "o-", color=col, ms=3.5, label=f"{key} case")
    ax.set_xlabel("input size $n$")
    ax.set_ylabel("time (s)")
    if log:
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_title("Measured times (log-log)")
    else:
        ax.set_title("Measured times (linear axes)")
    ax.legend(fontsize=8)
fig.tight_layout()
fig.savefig(os.path.join(FIG, "times.pdf"))
plt.close(fig)

# 3. Measured time divided by the case-(4) exact count
fig, ax = plt.subplots(figsize=(6.5, 3.4))
for key, curve, col in (("best", B4, "#15803d"), ("worst", W4, "#b91c1c"), ("average", A4, "#1d4ed8")):
    ratio = [times[key][n] / curve[n - 1] * 1e9 for n in sizes if n >= 10]
    ax.plot([n for n in sizes if n >= 10], ratio, "o-", color=col, ms=3.5,
            label=f"{key}: time / formula (4)")
ax.set_xlabel("input size $n$")
ax.set_ylabel("ns per basic operation (4)")
ax.set_title("Measured time divided by the case-(4) exact count")
ax.legend(fontsize=8)
fig.tight_layout()
fig.savefig(os.path.join(FIG, "ratio.pdf"))
plt.close(fig)

# 4. Theoretical counts of operation (4) on a logarithmic scale
fig, ax = plt.subplots(figsize=(6.5, 3.4))
ax.plot(dense, W4, color="#b91c1c", label=r"$W_4(n)\in\Theta(n^3)$")
ax.plot(dense, A4, color="#1d4ed8", label=r"$A_4(n)\in\Theta(n^2)$")
ax.plot(dense, B4, color="#15803d", label=r"$B_4(n)\in\Theta(n\log n)$")
ax.set_yscale("log")
ax.set_xlabel("input size $n$")
ax.set_ylabel("basic operations (4), log scale")
ax.set_title("Theoretical counts of operation (4)")
ax.legend(fontsize=8)
fig.tight_layout()
fig.savefig(os.path.join(FIG, "theory4.pdf"))
plt.close(fig)

print("figures written to", FIG)
