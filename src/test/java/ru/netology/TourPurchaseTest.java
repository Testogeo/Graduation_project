package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.page.PurchasePage;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.DataHelper.getPaymentInfo;
import java.util.HashMap;
import java.util.Map;

public class TourPurchaseTest {
    PurchasePage PurchasePage;
    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
        PurchasePage = open("http://localhost:8080", PurchasePage.class);
    }

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void openPage() {
        open("http://localhost:8080");
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
        databaseCleanUp();
    }

    @Nested
    //Тесты на оплату и получения кредита по валидной карте:
    public class ValidCard {

        @Test
        @SneakyThrows
        @DisplayName("'testValidCard' Pay Valid Card")
        public void shouldPaymentValidCard() {
            var purchasePage = new PurchasePage();
            purchasePage.cardPayment();
            var info = getApprovedCard();
            purchasePage.sendingData(info);
            //Время отправки данных в базу данных, в секундах:
            TimeUnit.SECONDS.sleep(10);
            var expected = "APPROVED";
            var paymentInfo = getPaymentInfo();
            var orderInfo = getOrderInfo();
            //Проверка соответствия статуса в базе данных в таблице покупок:
            assertEquals(expected, paymentInfo.getStatus());
            //Проверка соответствия в базе данных id в таблице покупок и в таблице заявок:
            assertEquals(paymentInfo.getTransaction_id(), orderInfo.getPayment_id());
            //Проверка вывода соответствующего уведомления пользователю на странице покупок:
            purchasePage.bankApproved();
        }

        @Test
        @SneakyThrows
        @DisplayName("'testValidCard' Credit To Valid Card")
        public void shouldCreditValidCard() {
            var purchasePage = new PurchasePage();
            purchasePage.cardCredit();
            var info = getApprovedCard();
            purchasePage.sendingData(info);
            //Время отправки данных в базу данных, в секундах:
            TimeUnit.SECONDS.sleep(10);
            var expected = "APPROVED";
            var creditRequestInfo = getCreditRequestInfo();
            var orderInfo = getOrderInfo();
            //Проверка соответствия статуса в базе данных в таблице запросов кредита:
            assertEquals(expected, creditRequestInfo.getStatus());
            //Проверка соответствия в базе данных id в таблице запросов кредита и в таблице заявок:
            assertEquals(creditRequestInfo.getBank_id(), orderInfo.getCredit_id());
            //Проверка вывода соответствующего уведомления пользователю на странице покупок:
            purchasePage.bankApproved();
        }
    }

    @Nested
    //Тесты на оплату и получения кредита по не валидной карте:
    public class InvalidCard {

        @Test
        @SneakyThrows
        @DisplayName("'testNoneValidCard' Pay None Valid Card")
        public void shouldPaymentInvalidCard() {
            var purchasePage = new PurchasePage();
            purchasePage.cardPayment();
            var info = getDeclinedCard();
            purchasePage.sendingData(info);
            //Время отправки данных в базу данных, в секундах:
            TimeUnit.SECONDS.sleep(10);
            var expected = "DECLINED";
            var paymentInfo = getPaymentInfo();
            var orderInfo = getOrderInfo();
            //Проверка соответствия статуса в базе данных в таблице покупок:
            assertEquals(expected, paymentInfo.getStatus());
            //Проверка соответствия в базе данных id в таблице покупок и в таблице заявок:
            assertEquals(paymentInfo.getTransaction_id(), orderInfo.getPayment_id());
            //Проверка вывода соответствующего уведомления пользователю на странице покупок:
            purchasePage.bankDeclined();
        }

        @Test
        @SneakyThrows
        @DisplayName("'testNoneValidCard' Credit To NoneValid Card")
        public void shouldCreditInvalidCard() {
            var purchasePage = new PurchasePage();
            purchasePage.cardCredit();
            var info = getDeclinedCard();
            purchasePage.sendingData(info);
            //Время отправки данных в базу данных, в секундах:
            TimeUnit.SECONDS.sleep(10);
            var expected = "DECLINED";
            var creditRequestInfo = getCreditRequestInfo();
            var orderInfo = getOrderInfo();
            //Проверка соответствия статуса в базе данных в таблице запросов кредита:
            assertEquals(expected, creditRequestInfo.getStatus());
            //Проверка соответствия в базе данных id в таблице запросов кредита и в таблице заявок:
            assertEquals(creditRequestInfo.getBank_id(), orderInfo.getCredit_id());
            //Проверка вывода соответствующего уведомления пользователю на странице покупок:
            purchasePage.bankApproved();
        }
    }

    @Nested
    //Тесты на валидацию полей платежной формы:
    public class PaymentFormFieldValidation {

        @BeforeEach
        public void setPayment() {
            var purchasePage = new PurchasePage();
            purchasePage.cardPayment();
        }

        @Test
        @DisplayName("'validationFields' Send empty form")
        public void shouldEmpty() {
            var purchasePage = new PurchasePage();
            purchasePage.emptyForm();
        }

        @Test
        @DisplayName("'validationFields' Field 'NumberCard', Empty Field")
        public void shouldEmptyCardNumberField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyCardNumberField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'NumberCard', Not Full Number Card")
        public void shouldCardWithIncompleteCardNumber() {
            var purchasePage = new PurchasePage();
            var info = getCardWithIncompleteCardNumber();
            purchasePage.invalidCardNumberField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Month', Empty Field")
        public void shouldEmptyMonthField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyMonthField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Month', Wrong Month")
        public void shouldCardWithOverdueMonth() {
            var purchasePage = new PurchasePage();
            var info = getCardWithOverdueMonth();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Month', WrongSet '00'")
        public void shouldCardWithLowerMonthValue() {
            var purchasePage = new PurchasePage();
            var info = getCardWithLowerMonthValue();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Month', Over Limit Set '13'")
        public void shouldCardWithGreaterMonthValue() {
            var purchasePage = new PurchasePage();
            var info = getCardWithGreaterMonthValue();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Year', Empty")
        public void shouldEmptyYearField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyYearField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Year', Overdue Year")
        public void shouldCardWithOverdueYear() {
            var purchasePage = new PurchasePage();
            var info = getCardWithOverdueYear();
            purchasePage.invalidYearField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Year', From Future Year")
        public void shouldCardWithYearFromFuture() {
            var purchasePage = new PurchasePage();
            var info = getCardWithYearFromFuture();
            purchasePage.invalidYearField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Owner', Empty")
        public void shouldEmptyOwnerField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyOwnerField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Owner', Space/Hyphen")
        public void shouldCardWithSpaceOrHyphenOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithSpaceOrHyphenOwner();
            purchasePage.invalidOwnerField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Owner', Special Symbols")
        public void shouldCardWithSpecialSymbolsOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithSpecialSymbolsOwner();
            purchasePage.invalidOwnerField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'Owner', Numbers")
        public void shouldCardWithNumbersOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithNumbersOwner();
            purchasePage.invalidOwnerField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'CVC/CVV', Empty")
        public void shouldEmptyCVCField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyCVCField(info);
        }

        @Test
        @DisplayName("'validationFields' Field 'CVC/CVV', Incomplete")
        public void shouldCardWithIncompleteCVC() {
            var purchasePage = new PurchasePage();
            var info = getCardWithIncompleteCVC();
            purchasePage.invalidCVCField(info);
        }
    }

    @Nested
    //Тесты на валидацию полей кредитной формы:
    public class CreditFormFieldValidation {

        @BeforeEach
        public void setPayment() {
            var purchasePage = new PurchasePage();
            purchasePage.cardCredit();
        }

        @Test
        @DisplayName("'validationFieldsForm' Send Empty Form")
        public void shouldEmpty() {
            var purchasePage = new PurchasePage();
            purchasePage.emptyForm();
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'NumberCard', Empty")
        public void shouldEmptyCardNumberField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyCardNumberField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'NumberCard', Incomplete")
        public void shouldCardWithIncompleteCardNumber() {
            var purchasePage = new PurchasePage();
            var info = getCardWithIncompleteCardNumber();
            purchasePage.invalidCardNumberField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Month', Empty")
        public void shouldEmptyMonthField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyMonthField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Month', Overdue")
        public void shouldCardWithOverdueMonth() {
            var purchasePage = new PurchasePage();
            var info = getCardWithOverdueMonth();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Month', Lower Month Value")
        public void shouldCardWithLowerMonthValue() {
            var purchasePage = new PurchasePage();
            var info = getCardWithLowerMonthValue();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Month', Greater Month Value")
        public void shouldCardWithGreaterMonthValue() {
            var purchasePage = new PurchasePage();
            var info = getCardWithGreaterMonthValue();
            purchasePage.invalidMonthField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Year', Empty")
        public void shouldEmptyYearField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyYearField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Year', Overdue")
        public void shouldCardWithOverdueYear() {
            var purchasePage = new PurchasePage();
            var info = getCardWithOverdueYear();
            purchasePage.invalidYearField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Year', FromFuture")
        public void shouldCardWithYearFromFuture() {
            var purchasePage = new PurchasePage();
            var info = getCardWithYearFromFuture();
            purchasePage.invalidYearField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Owner', Empty")
        public void shouldEmptyOwnerField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyOwnerField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Owner', Space/Hyphen")
        public void shouldCardWithSpaceOrHyphenOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithSpaceOrHyphenOwner();
            purchasePage.invalidOwnerField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'Owner', SpecialSymbols")
        public void shouldCardWithSpecialSymbolsOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithSpecialSymbolsOwner();
            purchasePage.invalidOwnerField(info);
        }
        @Test
        @DisplayName("'validationFieldsForm' Field 'Owner', SpecialSymbols")
        public void shouldCardWithRussianSymbolsOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithRussianSymbolsOwner();
            purchasePage.invalidOwnerField(info);}

        @Test
        @DisplayName("'validationFieldsForm' Field 'Owner', WithNumber")
        public void shouldCardWithNumbersOwner() {
            var purchasePage = new PurchasePage();
            var info = getCardWithNumbersOwner();
            purchasePage.invalidOwnerField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'CVC/CVV', Empty")
        public void shouldEmptyCVCField() {
            var purchasePage = new PurchasePage();
            var info = getApprovedCard();
            purchasePage.emptyCVCField(info);
        }

        @Test
        @DisplayName("'validationFieldsForm' Field 'CVC/CVV', Incomplete")
        public void shouldCardWithIncompleteCVC() {
            var purchasePage = new PurchasePage();
            var info = getCardWithIncompleteCVC();
            purchasePage.invalidCVCField(info);
        }
    }
}
