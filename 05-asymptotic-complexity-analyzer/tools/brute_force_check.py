"""Exhaustive check: for every n <= 9 enumerate all 3^n inputs and check that the
minimum, maximum and exact (probability-weighted) mean of each counter equal
B(n), W(n) and A(n), and that the chosen best/worst inputs attain them."""

import importlib.util
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
target = os.path.join(ROOT, "complexity_analyzer.py")

spec = importlib.util.spec_from_file_location("prog", target)
m = importlib.util.module_from_spec(spec)
spec.loader.exec_module(m)

if m.exhaustive_check(m.VARIANTS[2], max_n=9):
    print("all formulas verified for n = 1..9")
