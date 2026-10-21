# Marketplace Matching Simulation (GigMatch-Pro)

A fully functional freelance marketplace simulation system in Java, designed to efficiently manage interactions between customers and freelancers. Developed for a data structures and algorithms course at Boğaziçi University.

The platform models a realistic service marketplace where freelancers are ranked and matched dynamically using a composite scoring mechanism that incorporates skill levels, ratings, reliability, and burnout effects.

---

## Features

### User Management
- Registration of customers and freelancers with validation
- Unique ID enforcement across the platform
- Customer-specific freelancer blacklisting

### Service-Based Matching System
- Freelancers categorized into service buckets
- Priority-based matching using a custom **max-heap**
- Composite score calculation combining:
  - Weighted skills (service-specific importance)
  - Average rating
  - Reliability (cancellation ratio)
  - Burnout penalties

### Dynamic Job Allocation
- Customers can request top-k freelancers for a service
- Automatic assignment of the best candidate
- Manual employment option supported

### Freelancer Performance System
- Ratings update via incremental averaging
- Skill progression for high-rated jobs
- Skill degradation on cancellations
- Burnout detection based on monthly workload
- Automatic platform bans after excessive cancellations

### Customer Loyalty System
- Tiered loyalty levels (Bronze, Silver, Gold, Platinum)
- Discounts applied based on spending behavior
- Penalties for frequent cancellations

### Monthly Simulation Engine
- Applies queued service/price changes
- Updates burnout states
- Resets monthly counters
- Recomputes customer loyalty tiers

---

## Tech Stack

- **Language:** Java 17
- **Data Structures:**
  - Open-addressing hash maps (`StringMap`) for fast lookups
  - Dynamic arrays (`ObjList`) to avoid Java collection overhead
  - Binary max-heaps (`ServiceBucket`) with index tracking for efficient updates
- **Performance:**
  - Custom `FastScanner` for high-performance input parsing
  - Avoidance of Java standard collections in critical paths
  - O(log n) insertion and removal in service queues

---

## Local Setup

### Prerequisites
- Java JDK 17+

### Compile and Run
```bash
git clone https://github.com/yigitsarpavci/marketplace-matching-simulation.git
cd marketplace-matching-simulation
javac Main.java
java Main
```

---

## Design Highlights

This project demonstrates the design and implementation of a scalable backend system that simulates real-world marketplace dynamics. It highlights skills in:
- Data structures and algorithm design
- System modeling and state management
- Performance optimization in Java
- Clean and modular code organization

---

## License

This project is available under the terms specified in the repository.
