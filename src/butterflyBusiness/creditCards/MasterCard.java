package butterflyBusiness.creditCards;

import java.util.Arrays;

public class MasterCard extends CreditCard {

    public MasterCard() {
        super(Arrays.asList(
                new LengthValidator(Arrays.asList("16")),
                new StartsWithValidator(Arrays.asList("51", "52", "53", "54", "55","222100", "272099"))
        ));
    }

    @Override
    public String getName() {
        return "MasterCard";
    }

}
