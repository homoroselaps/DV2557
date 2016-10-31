package wumpusworld.aiclient.testing;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;




/**
 * Provides simple API for unit testing.
 * Created by Nejc on 17. 10. 2016.
 */
public class UnitTests {




    private static final List<TestObject<?>> testInheritance = new ArrayList<>();
    private static int passed = 0;
    private static int failed = 0;
    private static DescriptionItem currentTest;




    public static DescriptionItem it(String should) {
        return new DescriptionItem(should);
    }


    public static <T> TestObject<T> init(String description, T object) {
        return new TestObject<>(object, description);
    }


    public static TestObject<Object> init(String description) {
        return new TestObject<>(null, description);
    }


    private static void runExpect(DescriptionItem di, Supplier<Boolean> predicate) {
        if (predicate == null) {
            makeFail("Test: Predicate was null.");
            return;
        }
        if (di == null) {
            makeFail("Test: DescriptionItem was null.");
            return;
        }
        currentTest = null;
        if (di.getDescription().isPresent())
            write("Test: it " + di.getDescription().get().trim());
        currentTest = di;

        try {
            boolean res = predicate.get();
            if (res) {
                passed++;
            } else {
                if (testInheritance.size() == 0)
                    makeFail("IT " + di.getDescription());
                else
                    makeFail(testInheritance.get(testInheritance.size() - 1).getDescription().orElse("IT").toUpperCase() + " " + di.getDescription().orElse(" does not function correctly"));
            }
        } catch (Exception ex) {
            makeFail("Exception thrown: " + ex.getMessage());
        }
        currentTest = null;
    }


    private static void makeFail(String msg) {
        failed++;

//		TestObject testObject = testInheritance.stream().
//				findFirst()
//				.orElse(null);
//		List<DescriptionItem> list = failedList.getOrDefault(testObject, new ArrayList<>());
//		list.add(descriptionItem);
//		failedList.put(testObject, list);

        error(msg);
    }




    private static void write(String prefix, String msg) {
        if (prefix == null)
            System.out.print("    ");
        else
            System.out.print(prefix + ":");
        int tabs = testInheritance.size() + (currentTest == null ? 0 : 1);
        for (int i = 0; i < tabs; i++)
            System.out.print('\t');
        System.out.println(msg);
    }


    public static void write() {
        System.out.println();
    }


    public static void write(String msg) {
        write(null, msg);
    }


    private static void log(String msg) {
        write(null, msg);
    }


    private static void error(String msg) {
        write("ERR", msg);
    }


    public static void warn(String msg) {
        write("WRN", msg);
    }


    public static void printResults() {
        System.out.println();
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("Passed tests: " + passed + "/" + (passed + failed));
        System.out.println("Failed tests: " + failed + "/" + (passed + failed));
        System.out.println("------------------------------------------");
        if (failed == 0) {
            System.out.println("All test are passing! (yay)");
            System.out.println("------------------------------------------");
        }
    }








    public static class ExpectObject<T> {


        private final T object;
        private final DescriptionItem di;


        public T getObject() {
            return object;
        }


        public DescriptionItem getDescriptionItem() {
            return di;
        }


        public ExpectObject(DescriptionItem di, T object) {
            Objects.requireNonNull(di);
            this.di = di;
            this.object = object;
        }


        public ExpectObject<T> to(Expectation<T> predicate) {
            if (predicate == null)
                error("Predicate was null.");
            else
                runExpect(di, () -> predicate.expect(object));
            return this;
        }


        public ExpectObject<T> to(Supplier<Boolean> predicate) {
            runExpect(di, predicate);
            return this;
        }


        public ExpectObject<T> toBeNull() {
            runExpect(di, () -> object == null);
            return this;
        }


        public ExpectObject<T> notToBeNull() {
            runExpect(di, () -> object != null);
            return this;
        }


        public ExpectObject<T> toEqual(Object other) {
            runExpect(di, () -> Objects.equals(object, other));
            return this;
        }


        public ExpectObject<T> notToEqual(Object other) {
            runExpect(di, () -> Objects.equals(object, other));
            return this;
        }


        public ExpectObject<T> toThrow() {
            return toThrow(null);
        }


        public ExpectObject<T> toThrow(Class<?> throwType) {
            if (!(object instanceof Runnable)) {
                error("object must be an instance of Runnable interface.");
                return this;
            }
            runExpect(di, () -> {
                try {
                    ((Runnable) object).run();
                    return false;
                } catch (Exception ex) {
                    return throwType == null || throwType.isAssignableFrom(ex.getClass());
                }
            });
            return this;
        }


        public DescriptionItem and() {
            return di;
        }

    }




    public static class TestObject<T> {


        private final T object;
        private final String description;
        private Supplier<T> cloneMethod;
        private boolean skip;


        public T getObject() {
            return object;
        }


        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }


        public TestObject(T object, String description) {
            this.object = object;
            this.description = description;
        }


        public TestObject<T> skip(boolean doSkip) {
            this.skip = doSkip;
            return this;
        }


        public TestObject<T> skip() {
            this.skip = true;
            return this;
        }


        public TestObject<T> clone(String methodName) {
            if (methodName == null) {
                error("Method name must be given when calling .clone() on a TestObject.");
                return this;
            }
            cloneMethod = () -> cloneFromMethod(methodName);
            return this;
        }


        public TestObject<T> clone(Supplier<T> cloneMethod) {
            if (cloneMethod == null) {
                error("Method must be given when calling .clone() on a TestObject.");
                return this;
            }
            this.cloneMethod = cloneMethod;
            return this;
        }


        public TestObject<T> clone() {
            return clone("clone");
        }


        public TestObject<T> cancelClone() {
            cloneMethod = null;
            return this;
        }


        private T getObj() {
            if (cloneMethod == null || object == null)
                return object;

            try {
                return cloneMethod.get();
            } catch (Exception ex) {
                error("Object threw an exception while cloning: " + ex.getMessage());
            }

            return object;
        }


        private T cloneFromMethod(String methodName) {
            if (cloneMethod == null || object == null)
                return object;

            try {
                return (T) object.getClass().getMethod(methodName).invoke(object);
            } catch (Exception ex) {
                error("Object threw an exception while cloning: " + ex.getMessage());
            }

            return object;
        }


        public TestObject<T> test(Consumer<T> consumer) {
            test(() -> consumer.accept(getObj()));
            return this;
        }


        public TestObject<T> test(Runnable runnable) {
            if (skip) return this;
            write("Testing: " + (description == null ? "UNKNOWN" : description.toUpperCase()));
            testInheritance.add(this);
            runnable.run();
            testInheritance.remove(this);
            write();
            return this;
        }


    }




    public static class DescriptionItem {


        private final String description;


        public Optional<String> getDescription() {
            return java.util.Optional.ofNullable(description);
        }


        public DescriptionItem(String description) {
            this.description = description;
        }


        public <T> ExpectObject<T> expect(T obj) {
            return new ExpectObject<>(this, obj);
        }


        public ExpectObject<Runnable> expect(Runnable runnable) {
            return new ExpectObject<>(this, runnable);
        }


        public DescriptionItem expectTo(Supplier<Boolean> predicate) {
            runExpect(this, predicate);
            return this;
        }


    }




    public interface Expectation<T> {


        boolean expect(T obj);

    }


}
