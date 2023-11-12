package mx.kenzie.centurion;

public class LocalNumberArgument extends HashedArg<RelativeNumber> {

    public LocalNumberArgument() {
        super(RelativeNumber.class);
        this.label = "number";
    }

    @Override
    public boolean matches(String input) {
        if (!input.startsWith("^")) return false;
        try {
            this.lastValue = this.parseNew(input);
            this.lastHash = input.hashCode();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public RelativeNumber parseNew(String input) {
        if (input.equals("^")) return new RelativeNumber(0, true);
        return new RelativeNumber(Double.parseDouble(input.substring(1)), true);
    }

}
