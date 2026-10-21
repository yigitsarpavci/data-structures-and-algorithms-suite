import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Main operates all platform operations defined in the project spec (registration, employment, queries, etc.).
public class Main {

    // Skill weight profiles per service; each row sums the importance of 5 skills.
    private static final int[][] SERVICE_PROFILES = {
            {70, 60, 50, 85, 90},
            {95, 75, 85, 80, 90},
            {75, 85, 95, 70, 85},
            {50, 50, 30, 95, 95},
            {80, 95, 70, 90, 75},
            {40, 60, 40, 90, 85},
            {70, 85, 90, 80, 95},
            {85, 80, 90, 75, 90},
            {85, 65, 60, 90, 85},
            {90, 65, 70, 95, 95}
    };

    private static final int[][] SERVICE_SKILL_PRIORITY = initSkillPriority();

    private static final String[] SERVICE_NAMES = {
            "paint", "web_dev", "graphic_design", "data_entry", "tutoring",
            "cleaning", "writing", "photography", "plumbing", "electrical"
    };

    // Global registries.
    private static final StringMap<Customer> customers = new StringMap<>();
    private static final StringMap<Freelancer> freelancers = new StringMap<>();
    private static final StringMap<String> freelancerToCustomer = new StringMap<>();
    private static final ServiceBucket[] availableByService = new ServiceBucket[10];
    private static final ObjList<Freelancer> freelancerList = new ObjList<>();
    private static final ObjList<Customer> customerList = new ObjList<>();

    public static void main(String[] args) {
        if (args.length != 2) return;
        try (FastScanner fs = new FastScanner(args[0]);
             BufferedWriter out = new BufferedWriter(new FileWriter(args[1]), 1 << 20)) {
            String op;
            while ((op = fs.next()) != null) {
                switch (op) {
                    case "register_customer":    registerCustomer(fs, out);      break;
                    case "register_freelancer": registerFreelancer(fs, out);    break;
                    case "employ_freelancer":   employFreelancer(fs, out);      break;
                    case "request_job":         requestJob(fs, out);            break;
                    case "complete_and_rate":   completeAndRate(fs, out);       break;
                    case "cancel_by_customer":  cancelByCustomer(fs, out);      break;
                    case "cancel_by_freelancer":cancelByFreelancer(fs, out);    break;
                    case "blacklist":           blacklist(fs, out);             break;
                    case "unblacklist":         unblacklist(fs, out);           break;
                    case "change_service":      changeService(fs, out);         break;
                    case "simulate_month":      simulateMonth(out);             break;
                    case "query_freelancer":    queryFreelancer(fs, out);       break;
                    case "query_customer":      queryCustomer(fs, out);         break;
                    case "update_skill":        updateSkill(fs, out);           break;
                    default:
                }
            }
        } catch (IOException ignored) { }
    }

    // register_customer inserts a new customer id if it doesn't conflict with existing entities.
    private static void registerCustomer(FastScanner fs, BufferedWriter out) throws IOException {
        String id = fs.next();
        if (id == null || customers.containsKey(id) || freelancers.containsKey(id)) {
            out.write("Some error occurred in register_customer.");
            out.newLine();
            return;
        }
        Customer c = new Customer(id);
        customers.put(id, c);
        customerList.add(c);
        out.write("registered customer " + id);
        out.newLine();
    }

    // register_freelancer validates unique ids, service name, price, and bounded skills before adding to pools.
    private static void registerFreelancer(FastScanner fs, BufferedWriter out) throws IOException {
        String id = fs.next();
        String serviceRaw = fs.next();
        String priceStr = fs.next();
        if (id == null || serviceRaw == null || priceStr == null) {
            out.write("Some error occurred in register_freelancer.");
            out.newLine();
            return;
        }

        if (customers.containsKey(id) || freelancers.containsKey(id)) {
            consumeRest(fs, 5);
            out.write("Some error occurred in register_freelancer.");
            out.newLine();
            return;
        }

        String service = normalizeService(serviceRaw);
        int idx = serviceIndex(service);
        if (idx == -1) {
            consumeRest(fs, 5);
            out.write("Some error occurred in register_freelancer.");
            out.newLine();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            consumeRest(fs, 5);
            out.write("Some error occurred in register_freelancer.");
            out.newLine();
            return;
        }
        if (price <= 0) {
            consumeRest(fs, 5);
            out.write("Some error occurred in register_freelancer.");
            out.newLine();
            return;
        }

        int[] skills = new int[5];
        for (int i = 0; i < 5; i++) {
            String s = fs.next();
            if (s == null) {
                out.write("Some error occurred in register_freelancer.");
                out.newLine();
                return;
            }
            try {
                int v = Integer.parseInt(s);
                if (v < 0 || v > 100) {
                    consumeRest(fs, 4 - i);
                    out.write("Some error occurred in register_freelancer.");
                    out.newLine();
                    return;
                }
                skills[i] = v;
            } catch (NumberFormatException e) {
                consumeRest(fs, 4 - i);
                out.write("Some error occurred in register_freelancer.");
                out.newLine();
                return;
            }
        }

        Freelancer f = new Freelancer(id, service, price, skills);
        freelancers.put(id, f);
        freelancerList.add(f);
        addAvailable(f);
        out.write("registered freelancer " + id);
        out.newLine();
    }

    // employ_freelancer assigns an available freelancer to a customer manually.
    private static void employFreelancer(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        String fId = fs.next();
        if (cId == null || fId == null) {
            out.write("Some error occurred in employ_freelancer.");
            out.newLine();
            return;
        }
        Customer c = customers.get(cId);
        Freelancer f = freelancers.get(fId);
        if (c == null || f == null || f.platformBanned || !f.isAvailable || c.blacklist.contains(fId)) {
            out.write("Some error occurred in employ_freelancer.");
            out.newLine();
            return;
        }
        f.isAvailable = false;
        removeAvailable(f);
        freelancerToCustomer.put(fId, cId);
        c.employmentCount++;
        out.write(cId + " employed " + fId + " for " + f.service);
        out.newLine();
    }

    // request_job pops up to k best freelancers for a service, skipping blacklisted ones and auto-hires the top.
    private static void requestJob(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        String serviceRaw = fs.next();
        String kStr = fs.next();
        if (cId == null || serviceRaw == null || kStr == null) {
            out.write("Some error occurred in request_job.");
            out.newLine();
            return;
        }

        Customer c = customers.get(cId);
        String service = normalizeService(serviceRaw);
        int sIdx = serviceIndex(service);
        if (c == null || sIdx == -1) {
            out.write("Some error occurred in request_job.");
            out.newLine();
            return;
        }

        int k;
        try {
            k = Integer.parseInt(kStr);
        } catch (NumberFormatException e) {
            out.write("Some error occurred in request_job.");
            out.newLine();
            return;
        }

        ServiceBucket bucket = availableByService[sIdx];
        if (bucket == null || bucket.isEmpty()) {
            out.write("no freelancers available");
            out.newLine();
            return;
        }

        // popped keeps polled freelancers so we can push them back once selection ends.
        ObjList<Freelancer> popped = new ObjList<>();
        // candidates holds the blacklist-filtered subset presented to the customer.
        ObjList<Freelancer> candidates = new ObjList<>();

        while (candidates.size() < k && !bucket.isEmpty()) {
            Freelancer f = bucket.poll();
            popped.add(f);  // Keep track so we can restore heap order afterwards.
            if (c.blacklist.contains(f.id)) continue;  // Skip freelancers the customer refuses to work with.
            candidates.add(f);
        }

        if (candidates.size() == 0) {
            for (int i = 0; i < popped.size(); i++) bucket.add(popped.get(i));
            out.write("no freelancers available");
            out.newLine();
            return;
        }

        out.write("available freelancers for " + service + " (top " + candidates.size() + "):");
        out.newLine();
        for (int i = 0; i < candidates.size(); i++) {
            Freelancer f = candidates.get(i);
            out.write(f.id + " - composite: " + f.currentComposite +
                    ", price: " + f.price + ", rating: " + formatRatingList(f));
            out.newLine();
        }

        // Heap already returns highest-scoring freelancer first, so index 0 is optimal.
        Freelancer best = candidates.get(0);
        for (int i = 0; i < popped.size(); i++) {
            Freelancer f = popped.get(i);
            if (f != best) bucket.add(f);  // Push back everyone except the auto-hired freelancer.
        }

        best.isAvailable = false;
        freelancerToCustomer.put(best.id, cId);
        c.employmentCount++;
        out.write("auto-employed best freelancer: " + best.id + " for customer " + cId);
        out.newLine();
    }

    // complete_and_rate finalizes an employment, updates loyalty spend, ratings, skills, and availability.
    private static void completeAndRate(FastScanner fs, BufferedWriter out) throws IOException {
        String fId = fs.next();
        String ratingStr = fs.next();
        if (fId == null || ratingStr == null) {
            out.write("Some error occurred in complete_and_rate.");
            out.newLine();
            return;
        }
        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (NumberFormatException e) {
            out.write("Some error occurred in complete_and_rate.");
            out.newLine();
            return;
        }

        Freelancer f = freelancers.get(fId);
        String cId = freelancerToCustomer.get(fId);
        if (f == null || cId == null || rating < 0 || rating > 5) {
            out.write("Some error occurred in complete_and_rate.");
            out.newLine();
            return;
        }

        // job completes successfully, meaning the employment link disappears
        freelancerToCustomer.remove(fId);
        Customer c = customers.get(cId);
        if (c != null) c.totalSpent += paymentWithDiscount(c.loyaltyTier, f.price);  // Loyalty tier discount.

        // Incremental average avoids storing the full ratings list.
        f.ratingAvg = (f.ratingAvg * f.ratingCount + rating) / (f.ratingCount + 1);
        f.ratingCount++;
        f.completedCount++;
        f.completionsThisMonth++;
        if (rating >= 4) applySkillGain(f);
        f.currentComposite = computeCompositeInt(f, f.service);
        f.isAvailable = !f.platformBanned;
        if (f.isAvailable) addAvailable(f);
        out.write(fId + " completed job for " + cId + " with rating " + rating);
        out.newLine();
    }

    // cancel_by_customer releases the freelancer while tracking penalties for future loyalty calculations.
    private static void cancelByCustomer(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        String fId = fs.next();
        if (cId == null || fId == null) {
            out.write("Some error occurred in cancel_by_customer.");
            out.newLine();
            return;
        }
        Freelancer f = freelancers.get(fId);
        String cur = freelancerToCustomer.get(fId);
        if (f == null || cur == null || !cur.equals(cId)) {
            out.write("Some error occurred in cancel_by_customer.");
            out.newLine();
            return;
        }
        freelancerToCustomer.remove(fId);
        Customer c = customers.get(cId);
        if (c != null) c.cancelledByCustomer++;
        f.isAvailable = !f.platformBanned;
        if (f.isAvailable) addAvailable(f);
        out.write("cancelled by customer: " + cId + " cancelled " + fId);
        out.newLine();
    }

    // cancel_by_freelancer enforces rating penalty, skill decay, and potential platform bans for repeat offenders.
    private static void cancelByFreelancer(FastScanner fs, BufferedWriter out) throws IOException {
        String fId = fs.next();
        if (fId == null) {
            out.write("Some error occurred in cancel_by_freelancer.");
            out.newLine();
            return;
        }
        Freelancer f = freelancers.get(fId);
        String cId = freelancerToCustomer.get(fId);
        if (f == null || cId == null) {
            out.write("Some error occurred in cancel_by_freelancer.");
            out.newLine();
            return;
        }
        // Remove the assignment regardless of who cancels.
        freelancerToCustomer.remove(fId);
        // Treat freelancer-initiated cancellation as receiving a zero-star rating.
        f.ratingAvg = (f.ratingAvg * f.ratingCount) / (f.ratingCount + 1);  // Penalize by adding a zero rating.
        f.ratingCount++;
        f.cancelledCount++;
        f.cancellationsThisMonth++;
        degradeSkills(f);
        f.currentComposite = computeCompositeInt(f, f.service);
        f.isAvailable = !f.platformBanned;
        if (f.isAvailable) addAvailable(f);
        out.write("cancelled by freelancer: " + fId + " cancelled " + cId);
        out.newLine();
        if (!f.platformBanned && f.cancellationsThisMonth >= 5) {
            f.platformBanned = true;
            f.isAvailable = false;
            removeAvailable(f);
            out.write("platform banned freelancer: " + fId);
            out.newLine();
        }
    }

    // blacklist prevents a customer from seeing/employing a specific freelancer in future requests.
    private static void blacklist(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        String fId = fs.next();
        if (cId == null || fId == null) {
            out.write("Some error occurred in blacklist.");
            out.newLine();
            return;
        }
        Customer c = customers.get(cId);
        Freelancer f = freelancers.get(fId);
        if (c == null || f == null || c.blacklist.contains(fId)) {
            out.write("Some error occurred in blacklist.");
            out.newLine();
            return;
        }
        c.blacklist.add(fId);
        out.write(cId + " blacklisted " + fId);
        out.newLine();
    }

    // unblacklist removes a freelancer from the customer's exclude list.
    private static void unblacklist(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        String fId = fs.next();
        if (cId == null || fId == null) {
            out.write("Some error occurred in unblacklist.");
            out.newLine();
            return;
        }
        Customer c = customers.get(cId);
        Freelancer f = freelancers.get(fId);
        if (c == null || f == null || !c.blacklist.contains(fId)) {
            out.write("Some error occurred in unblacklist.");
            out.newLine();
            return;
        }
        c.blacklist.remove(fId);
        out.write(cId + " unblacklisted " + fId);
        out.newLine();
    }

    // change_service queues a deferred service/price switch that becomes effective during simulate_month.
    private static void changeService(FastScanner fs, BufferedWriter out) throws IOException {
        String fId = fs.next();
        String servRaw = fs.next();
        String priceStr = fs.next();
        if (fId == null || servRaw == null || priceStr == null) {
            out.write("Some error occurred in change_service.");
            out.newLine();
            return;
        }

        Freelancer f = freelancers.get(fId);
        String newService = normalizeService(servRaw);
        int idx = serviceIndex(newService);
        if (f == null || idx == -1) {
            out.write("Some error occurred in change_service.");
            out.newLine();
            return;
        }

        int newPrice;
        try {
            newPrice = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            out.write("Some error occurred in change_service.");
            out.newLine();
            return;
        }

        if (newPrice <= 0 || f.service.equals(newService)) {
            out.write("Some error occurred in change_service.");
            out.newLine();
            return;
        }

        f.hasQueuedChange = true;  // Spec requires applying service switches during simulate_month.
        f.queuedService = newService;
        f.queuedPrice = newPrice;
        out.write("service change for " + fId + " queued from " + f.service + " to " + newService);
        out.newLine();
    }

    // simulate_month applies queued service changes, evaluates burnout, resets counters, and updates loyalty tiers.
    private static void simulateMonth(BufferedWriter out) throws IOException {
        // First, apply queued service/price changes.
        for (int i = 0; i < freelancerList.size(); i++) {
            Freelancer f = freelancerList.get(i);
            if (f.hasQueuedChange) {
                if (f.isAvailable) removeAvailable(f);
                f.service = f.queuedService;
                f.price = f.queuedPrice;
                f.currentComposite = computeCompositeInt(f, f.service);
                f.hasQueuedChange = false;
                f.queuedService = null;
                f.queuedPrice = 0;
            }
        }

        // Second, evaluate burnout thresholds and refresh monthly counters.
        for (int i = 0; i < freelancerList.size(); i++) {
            Freelancer f = freelancerList.get(i);
            if (!f.isBurnout && f.completionsThisMonth >= 5) {
                f.isBurnout = true;
            } else if (f.isBurnout && f.completionsThisMonth <= 2) {
                f.isBurnout = false;
            }
            f.completionsThisMonth = 0;
            f.cancellationsThisMonth = 0;
            if (f.isAvailable && !f.platformBanned) {
                removeAvailable(f);
                f.currentComposite = computeCompositeInt(f, f.service);
                addAvailable(f);
            }
        }

        // Finally, loyalty tiers depend on lifetime spend minus penalties.
        for (int i = 0; i < customerList.size(); i++) {
            updateLoyalty(customerList.get(i));
        }

        out.write("month complete");
        out.newLine();
    }

    // query_freelancer prints the snapshot of freelancer state for debugging/testing.
    private static void queryFreelancer(FastScanner fs, BufferedWriter out) throws IOException {
        String fId = fs.next();
        if (fId == null) {
            out.write("Some error occurred in query_freelancer.");
            out.newLine();
            return;
        }
        Freelancer f = freelancers.get(fId);
        if (f == null) {
            out.write("Some error occurred in query_freelancer.");
            out.newLine();
            return;
        }
        out.write(f.id + ": " + f.service + ", price: " + f.price + ", rating: " + formatRatingQuery(f) +
                ", completed: " + f.completedCount + ", cancelled: " + f.cancelledCount +
                ", skills: (" + f.skills[0] + "," + f.skills[1] + "," + f.skills[2] + "," +
                f.skills[3] + "," + f.skills[4] + ")" +
                ", available: " + (f.isAvailable ? "yes" : "no") +
                ", burnout: " + (f.isBurnout ? "yes" : "no"));
        out.newLine();
    }

    // query_customer prints spend, loyalty tier, blacklist size, and employment counts.
    private static void queryCustomer(FastScanner fs, BufferedWriter out) throws IOException {
        String cId = fs.next();
        if (cId == null) {
            out.write("Some error occurred in query_customer.");
            out.newLine();
            return;
        }
        Customer c = customers.get(cId);
        if (c == null) {
            out.write("Some error occurred in query_customer.");
            out.newLine();
            return;
        }
        out.write(c.id + ": total spent: $" + c.totalSpent +
                ", loyalty tier: " + c.loyaltyTier +
                ", blacklisted freelancer count: " + c.blacklist.size() +
                ", total employment count: " + c.employmentCount);
        out.newLine();
    }

    // update_skill replaces all five skill values at once, with validation and heap refresh.
    private static void updateSkill(FastScanner fs, BufferedWriter out) throws IOException {
        String fId = fs.next();
        if (fId == null) {
            out.write("Some error occurred in update_skill.");
            out.newLine();
            return;
        }
        Freelancer f = freelancers.get(fId);

        boolean error = false;
        int[] newSkills = new int[5];
        for (int i = 0; i < 5; i++) {
            String s = fs.next();
            if (s == null) {
                error = true;
                break;
            }
            try {
                int v = Integer.parseInt(s);
                if (v < 0 || v > 100) error = true;
                newSkills[i] = v;
            } catch (NumberFormatException e) {
                error = true;
            }
        }

        if (f == null || error) {
            out.write("Some error occurred in update_skill.");
            out.newLine();
            return;
        }

        boolean wasAvailable = f.isAvailable;  // Preserve heap order if freelancer was offerable.
        if (wasAvailable) removeAvailable(f);
        System.arraycopy(newSkills, 0, f.skills, 0, 5);
        f.currentComposite = computeCompositeInt(f, f.service);
        if (wasAvailable && !f.platformBanned) addAvailable(f);
        out.write("updated skills of " + fId + " for " + f.service);
        out.newLine();
    }

    // Utility to discard trailing tokens on malformed commands so parsing realigns.
    private static void consumeRest(FastScanner fs, int n) {
        for (int i = 0; i < n; i++) fs.next();
    }

    private static void addAvailable(Freelancer f) {
        if (f.platformBanned) return;
        int idx = serviceIndex(f.service);
        if (idx == -1) return;
        ServiceBucket bucket = availableByService[idx];
        if (bucket == null) {
            bucket = new ServiceBucket();
            availableByService[idx] = bucket;
        }
        f.currentComposite = computeCompositeInt(f, f.service);  // Refresh composite before re-queuing.
        bucket.add(f);
    }

    private static void removeAvailable(Freelancer f) {
        int idx = serviceIndex(f.service);
        if (idx == -1) return;
        ServiceBucket bucket = availableByService[idx];
        if (bucket != null) bucket.remove(f.id);
    }

    // Services are case-insensitive and treat '-' same as '_'; normalize to canonical form.
    private static String normalizeService(String raw) {
        if (raw == null) return null;
        return raw.toLowerCase().replace('-', '_');
    }

    // Maps normalized service string back to SERVICE_PROFILES row index (or -1 if invalid).
    private static int serviceIndex(String s) {
        if (s == null) return -1;
        for (int i = 0; i < SERVICE_NAMES.length; i++) {
            if (SERVICE_NAMES[i].equals(s)) return i;
        }
        return -1;
    }

    // Composite score mimics the spec formula: skills + rating + reliability − burnout penalty.
    private static int computeCompositeInt(Freelancer f, String service) {
        int idx = serviceIndex(service);
        if (idx == -1) return 0;
        int[] prof = SERVICE_PROFILES[idx];
        long dot = 0;
        long sum = 0;
        for (int i = 0; i < 5; i++) {
            dot += (long) f.skills[i] * prof[i];
            sum += prof[i];
        }
        double composite = getComposite(f, sum, (double) dot);
        if (composite < 0) composite = 0;
        return (int) Math.floor(composite * 10000.0);
    }

    private static double getComposite(Freelancer f, long sum, double dot) {
        double skillScore = sum == 0 ? 0.0 : dot / (100.0 * sum);  // Weighted average in [0,1].
        double ratingScore = f.getAverageRating() / 5.0;  // Normalize star rating to [0,1].
        int totalJobs = f.completedCount + f.cancelledCount;
        double reliability = totalJobs == 0 ? 1.0 : 1.0 - ((double) f.cancelledCount / totalJobs);  // cancellation ratio.
        double penalty = f.isBurnout ? 0.45 : 0.0;
        return 0.55 * skillScore + 0.25 * ratingScore + 0.20 * reliability - penalty;
    }

    // Apply monthly skill boosts prioritizing each service's most important attributes.
    private static void applySkillGain(Freelancer f) {
        int idx = serviceIndex(f.service);
        if (idx == -1) return;
        int[] order = SERVICE_SKILL_PRIORITY[idx];
        incrementSkill(f, order[0], 2);  // Emphasize the most important attribute.
        incrementSkill(f, order[1], 1);
        incrementSkill(f, order[2], 1);
    }

    // Helper used by skill gain to clamp skills to 100.
    private static void incrementSkill(Freelancer f, int idx, int delta) {
        int v = f.skills[idx] + delta;
        if (v > 100) v = 100;
        f.skills[idx] = v;
    }

    // Cancelling a job erodes every skill by three points (bounded at zero) to simulate falling behind.
    private static void degradeSkills(Freelancer f) {
        for (int i = 0; i < 5; i++) {
            int v = f.skills[i] - 3;
            if (v < 0) v = 0;
            f.skills[i] = v;
        }
    }

    // Loyalty tiers depend on net points (spend minus customer cancellations * 250).
    private static void updateLoyalty(Customer c) {
        long points = c.totalSpent - (long) c.cancelledByCustomer * 250;
        if (points < 0) points = 0;
        if (points >= 5000) c.loyaltyTier = "PLATINUM";      // highest discount bracket
        else if (points >= 2000) c.loyaltyTier = "GOLD";
        else if (points >= 500) c.loyaltyTier = "SILVER";
        else c.loyaltyTier = "BRONZE";
    }

    // Applies loyalty tier discount schedule and floors to whole dollars as required by the spec.
    private static int paymentWithDiscount(String tier, int price) {
        double disc = "PLATINUM".equals(tier) ? 0.15 :
                "GOLD".equals(tier) ? 0.10 :
                        "SILVER".equals(tier) ? 0.05 : 0.0;
        return (int) Math.floor(price * (1.0 - disc));
    }

    private static String formatRatingQuery(Freelancer f) {
        return formatRating(f);
    }

    private static String formatRatingList(Freelancer f) {
        return formatRating(f);
    }

    // Formats ratings to one decimal place using half-up rounding to match expected outputs.
    private static String formatRating(Freelancer f) {
        double avg = f.getAverageRating();
        BigDecimal bd = new BigDecimal(Double.toString(avg)).setScale(1, RoundingMode.HALF_UP);
        return bd.toPlainString();
    }

    // Heap comparator: higher composite first, then lexicographically smaller id for deterministic order.
    private static boolean better(Freelancer a, Freelancer b) {
        if (b == null) return true;
        if (a.currentComposite != b.currentComposite) return a.currentComposite > b.currentComposite;
        return a.id.compareTo(b.id) < 0;
    }

    private static int[][] initSkillPriority() {
        int[][] priority = new int[SERVICE_PROFILES.length][5];
        for (int i = 0; i < SERVICE_PROFILES.length; i++) {
            int[] order = {0, 1, 2, 3, 4};
            int[] profile = SERVICE_PROFILES[i];
            for (int j = 0; j < order.length; j++) {
                int best = j;
                for (int k = j + 1; k < order.length; k++) {
                    int a = order[k];
                    int b = order[best];
                    if (profile[a] > profile[b] || (profile[a] == profile[b] && a < b)) {
                        best = k;
                    }
                }
                int tmp = order[j];
                order[j] = order[best];
                order[best] = tmp;
            }
            System.arraycopy(order, 0, priority[i], 0, order.length);
        }
        return priority;
    }

    private static class Customer {
        final String id;
        long totalSpent = 0;
        int cancelledByCustomer = 0;
        String loyaltyTier = "BRONZE";
        final StringSet blacklist = new StringSet();
        int employmentCount = 0;
        Customer(String id) { this.id = id; }
    }

    private static class Freelancer implements Comparable<Freelancer> {
        final String id;
        String service;
        int price;
        int[] skills;
        double ratingAvg = 5.0;
        int ratingCount = 1;
        int completedCount = 0;
        int cancelledCount = 0;
        int completionsThisMonth = 0;
        int cancellationsThisMonth = 0;
        boolean isAvailable = true;
        boolean isBurnout = false;
        boolean platformBanned = false;
        boolean hasQueuedChange = false;
        String queuedService;
        int queuedPrice;
        int currentComposite = 0;

        Freelancer(String id, String service, int price, int[] skills) {
            this.id = id;
            this.service = service;
            this.price = price;
            this.skills = skills;
        }

        double getAverageRating() { return ratingAvg; }

        @Override
        public int compareTo(Freelancer o) {
            if (currentComposite != o.currentComposite) {
                return Integer.compare(o.currentComposite, currentComposite);
            }
            return id.compareTo(o.id);
        }
    }

    // Simple dynamic array list to avoid java.util overhead in tight loops.
    private static class ObjList<T> {
        Object[] data = new Object[16];
        int size = 0;

        void add(T val) {
            ensure(size + 1);
            data[size++] = val;
        }

        @SuppressWarnings("unchecked")
        T get(int idx) { return (T) data[idx]; }

        int size() { return size; }

        private void ensure(int cap) {
            if (cap <= data.length) return;
            int n = data.length << 1;
            if (n < cap) n = cap;
            Object[] nd = new Object[n];
            System.arraycopy(data, 0, nd, 0, size);
            data = nd;
        }
    }

    // Lightweight open-address hash map tuned for predictable iteration and no boxing.
    private static class StringMap<V> {
        private static final int INIT = 1 << 17;
        private String[] keys = new String[INIT];
        private Object[] values = new Object[INIT];
        private boolean[] used = new boolean[INIT];
        private int size = 0;

        private int hash(String s) {
            int h = s.hashCode();
            h ^= (h >>> 16);
            return h & (keys.length - 1);
        }

        // Expand hash table and redistribute entries (linear probing/rehashing).
        private void rehash() {
            String[] oldK = keys;
            Object[] oldV = values;
            boolean[] oldU = used;
            int newN = keys.length << 1;
            keys = new String[newN];
            values = new Object[newN];
            used = new boolean[newN];
            for (int i = 0; i < oldK.length; i++) {
                if (!oldU[i]) continue;
                String k = oldK[i];
                Object v = oldV[i];
                int idx = k.hashCode();
                idx ^= (idx >>> 16);
                idx &= (newN - 1);
                while (used[idx]) idx = (idx + 1) & (newN - 1);
                used[idx] = true;
                keys[idx] = k;
                values[idx] = v;
            }
        }

        void put(String key, V value) {
            if ((size << 1) >= keys.length) rehash();
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) {
                    values[idx] = value;
                    return;
                }
                idx = (idx + 1) & (keys.length - 1);
            }
            used[idx] = true;
            keys[idx] = key;
            values[idx] = value;
            size++;
        }

        @SuppressWarnings("unchecked")
        V get(String key) {
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) return (V) values[idx];
                idx = (idx + 1) & (keys.length - 1);
            }
            return null;
        }

        boolean containsKey(String key) {
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) return true;
                idx = (idx + 1) & (keys.length - 1);
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        // Open addressing deletion: remove slot then reinsert subsequent cluster to maintain probe sequence.
        // Same deletion strategy as StringMap (remove slot then re-add elements in cluster).
        void remove(String key) {
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) {
                    used[idx] = false;
                    keys[idx] = null;
                    values[idx] = null;
                    size--;
                    idx = (idx + 1) & (keys.length - 1);
                    while (used[idx]) {
                        String k = keys[idx];
                        Object v = values[idx];
                        used[idx] = false;
                        keys[idx] = null;
                        values[idx] = null;
                        size--;
                        put(k, (V) v);
                        idx = (idx + 1) & (keys.length - 1);
                    }
                    return;
                }
                idx = (idx + 1) & (keys.length - 1);
            }
        }
    }

    // Minimal set implementation used for customer blacklists and position tracking.
    private static class StringSet {
        private static final int INIT = 1 << 4;
        private String[] keys = new String[INIT];
        private boolean[] used = new boolean[INIT];
        private int count = 0;

        private int hash(String s) {
            int h = s.hashCode();
            h ^= (h >>> 16);
            return h & (keys.length - 1);
        }

        private void rehash() {
            String[] oldK = keys;
            boolean[] oldU = used;
            int newN = keys.length << 1;
            keys = new String[newN];
            used = new boolean[newN];
            for (int i = 0; i < oldK.length; i++) {
                if (!oldU[i]) continue;
                String k = oldK[i];
                int idx = k.hashCode();
                idx ^= (idx >>> 16);
                idx &= (newN - 1);
                while (used[idx]) idx = (idx + 1) & (newN - 1);
                used[idx] = true;
                keys[idx] = k;
            }
        }

        void add(String key) {
            if ((count << 1) >= keys.length) rehash();
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) return;
                idx = (idx + 1) & (keys.length - 1);
            }
            used[idx] = true;
            keys[idx] = key;
            count++;
        }

        boolean contains(String key) {
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) return true;
                idx = (idx + 1) & (keys.length - 1);
            }
            return false;
        }

        void remove(String key) {
            int idx = hash(key);
            while (used[idx]) {
                if (keys[idx].equals(key)) {
                    used[idx] = false;
                    keys[idx] = null;
                    count--;
                    idx = (idx + 1) & (keys.length - 1);
                    while (used[idx]) {
                        String k = keys[idx];
                        used[idx] = false;
                        keys[idx] = null;
                        count--;
                        add(k);
                        idx = (idx + 1) & (keys.length - 1);
                    }
                    return;
                }
                idx = (idx + 1) & (keys.length - 1);
            }
        }

        int size() { return count; }
    }

    // Binary max-heap per service that keeps freelancers sorted by composite score.
    private static class ServiceBucket {
        Freelancer[] heap = new Freelancer[16];
        int size = 0;
        // pos maps freelancer id to its current index so we can remove arbitrary entries in O(log n).
        final StringMap<IntBox> pos = new StringMap<>();

        boolean isEmpty() { return size == 0; }

        // Typical heap insert with duplicate guard.
        void add(Freelancer f) {
            if (pos.containsKey(f.id)) return;
            ensure(size + 1);
            heap[size] = f;
            pos.put(f.id, new IntBox(size));
            heapifyUp(size++);
        }

        // Remove and return the best freelancer (root of heap).
        Freelancer poll() {
            if (size == 0) return null;
            Freelancer res = heap[0];
            remove(res.id);
            return res;
        }

        // Arbitrary removal used when a freelancer becomes unavailable or switches services.
        void remove(String id) {
            IntBox box = pos.get(id);
            if (box == null) return;
            int idx = box.value;
            pos.remove(id);
            size--;
            if (idx == size) {
                heap[idx] = null;
                return;
            }
            heap[idx] = heap[size];
            heap[size] = null;
            pos.put(heap[idx].id, new IntBox(idx));
            heapifyDown(idx);
            heapifyUp(idx);
        }

        // Restore heap order after inserting at the end.
        private void heapifyUp(int idx) {
            while (idx > 0) {
                int p = (idx - 1) >> 1;
                if (!better(heap[idx], heap[p])) break;
                swap(idx, p);
                idx = p;
            }
        }

        // Restore heap order after removing/replacing the root.
        private void heapifyDown(int idx) {
            while (true) {
                int l = (idx << 1) + 1;
                int r = l + 1;
                int best = idx;
                if (l < size && better(heap[l], heap[best])) best = l;
                if (r < size && better(heap[r], heap[best])) best = r;
                if (best == idx) break;
                swap(idx, best);
                idx = best;
            }
        }

        private void swap(int i, int j) {
            Freelancer tmp = heap[i];
            heap[i] = heap[j];
            heap[j] = tmp;
            pos.put(heap[i].id, new IntBox(i));  // Update cached positions after swap.
            pos.put(heap[j].id, new IntBox(j));
        }

        // Grow heap storage (doubling strategy) when inserting beyond current capacity.
        private void ensure(int cap) {
            if (cap <= heap.length) return;
            int n = heap.length << 1;
            if (n < cap) n = cap;
            Freelancer[] nd = new Freelancer[n];
            System.arraycopy(heap, 0, nd, 0, size);
            heap = nd;
        }
    }

    // Tiny mutable wrapper so we can store heap indices in StringMap and update in place.
    private static class IntBox {
        int value;
        IntBox(int v) { value = v; }
    }

    // Custom fast scanner tuned for big input files (no regex, buffered byte reads).
    private static class FastScanner implements Closeable {
        private final byte[] buf = new byte[1 << 16];
        private int ptr = 0, len = 0;
        private final FileInputStream in;

        FastScanner(String path) throws IOException {
            in = new FileInputStream(path);
        }

        // Buffered byte reader; returns -1 on EOF.
        private int read() throws IOException {
            if (ptr >= len) {
                len = in.read(buf);
                ptr = 0;
                if (len <= 0) return -1;
            }
            return buf[ptr++] & 0xff;
        }

        // Reads next whitespace-separated token, mirroring Scanner but faster.
        String next() {
            try {
                int c;
                do { c = read(); } while (c <= 32 && c != -1);
                if (c == -1) return null;
                StringBuilder sb = new StringBuilder();
                while (c > 32) {
                    sb.append((char) c);
                    c = read();
                }
                return sb.toString();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
