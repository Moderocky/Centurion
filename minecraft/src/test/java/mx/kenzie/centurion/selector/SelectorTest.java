package mx.kenzie.centurion.selector;

import mx.kenzie.centurion.Arguments;
import mx.kenzie.centurion.TestCommandSender;
import org.junit.Test;

import java.util.List;

public class SelectorTest {

    private static final Finder<Integer> numbers = Finder.fixed("numbers", 1, 2, 3, 4, 5, 6, 7, 8, 9),
        odds = Finder.fixed("odds", 1, 3, 5, 7, 9),
        evens = Finder.fixed("evens", 2, 4, 6, 8);
    private static final Criterion<Integer, Integer> under = Criterion.of("under", Arguments.INTEGER,
        (integer, o) -> integer < o), over = Criterion.of("over", Arguments.INTEGER,
        (integer, o) -> integer > o);

    @Test
    // find numbers < 3 from a set of "all numbers"
    public void simple() {
        final Criterion<Integer, Integer> criterion = Criterion.of("under", Arguments.INTEGER,
            (integer, o) -> integer < o);
        final Filter<Integer> filter = criterion.filter("3");
        final Selector<Integer> selector = new Selector<>(numbers, filter);
        final List<Integer> list = selector.getAll(new TestCommandSender());
        assert list != null;
        assert !list.isEmpty();
        assert list.contains(1);
        assert list.contains(2);
        assert !list.contains(5);
        assert list.size() == 2;
    }

    @Test
    // find numbers > 4 from a set of "all numbers"
    public void universe() {
        final Universe<Integer> universe = Universe.of(numbers, odds, evens, under, over);
        final Selector<Integer> selector = new SelectorParser<>("@numbers[over=4]", Integer.class, universe).parse();
        final List<Integer> list = selector.getAll(new TestCommandSender());
        assert list != null;
        assert !list.isEmpty();
        assert list.equals(List.of(5, 6, 7, 8, 9)) : list;
    }

    @Test
    // find numbers > 3 & < 8 from a set of "even numbers"
    public void twoCriteria() {
        final Universe<Integer> universe = Universe.of(numbers, odds, evens, under, over);
        final Selector<Integer> selector = new SelectorParser<>("@evens[over=3,under=8]", Integer.class,
            universe).parse();
        final List<Integer> list = selector.getAll(new TestCommandSender());
        assert list != null;
        assert !list.isEmpty();
        assert list.equals(List.of(4, 6)) : list;
    }

}
