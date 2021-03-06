package test.integration.com.pyxis.petstore.persistence;

import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.Builder;
import test.support.com.pyxis.petstore.builders.ProductBuilder;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.DatabaseCleaner;
import test.support.com.pyxis.petstore.db.IntegrationTestContext;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.db.IntegrationTestContext.integrationTesting;

public class PersistentProductCatalogTest {

    IntegrationTestContext context = integrationTesting();

    Database database = new Database(context.openConnection());
    ProductCatalog productCatalog = context.getComponent(ProductCatalog.class);

    @After
    public void cleanDatabase() {
        new DatabaseCleaner(database).clean();
        database.close();
    }

    @Test public void
    wontFindAnythingIfNoProductMatches() throws Exception {
        database.given(aProduct().withName("Dalmatian").describedAs("A big dog"));

        Collection<Product> matchingProducts = productCatalog.findByKeyword("bulldog");
        assertThat("matching products", matchingProducts, is(empty()));
    }

    private Matcher<Collection<Product>> empty() {
        return Matchers.empty();
    }

    @SuppressWarnings("unchecked")
    @Test public void
    canFindProductsByMatchingName() throws Exception {
        database.given(aProduct().withName("English Bulldog"), and(aProduct().withName("French Bulldog")), and(aProduct().withName("Labrador Retriever")));

        Collection<Product> matches = productCatalog.findByKeyword("bull");
        assertThat("matching products", matches, hasSize(equalTo(2)));
        assertThat("matches", matches, containsProducts(productNamed("English Bulldog"), productNamed("French Bulldog")));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    canFindProductsByMatchingDescription() throws Exception {
        database.given(aProduct().withName("Labrador").describedAs("Friendly"), and(aProduct().withName("Golden").describedAs("Kids best friend")), and(aProduct().withName("Poodle").describedAs("Annoying")));

        List<Product> matches = productCatalog.findByKeyword("friend");
        assertThat("matching products", matches, hasSize(equalTo(2)));
        assertThat("matches", matches, containsProducts(productNamed("Labrador"), productNamed("Golden")));
    }

    @Test(expected = ConstraintViolationException.class)
    public void cannotPersistAProductWithoutAName() throws Exception {
        productCatalog.add(aProduct().withoutAName().build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void cannotPersistAProductWithoutANumber() throws Exception {
        productCatalog.add(aProduct().withoutANumber().build());
    }

    @Test public void
    canRoundTripProducts() throws Exception {
        final Collection<Product> sampleProducts = Arrays.asList(
                aProduct().withName("Labrador").describedAs("Labrador Retriever").withPhoto("labrador.png").build(),
                aProduct().withName("Dalmatian").build());

        for (Product product : sampleProducts) {
            productCatalog.add(product);
            database.assertCanBeReloadedWithSameState(product);
        }
    }

    @Test public void
    productNumberShouldBeUnique() throws Exception {
        ProductBuilder someProduct = aProduct().withNumber("LAB-1234");
        database.persist(someProduct);
        try {
            productCatalog.add(someProduct.build());
            fail("No constraint violation");
        } catch (org.hibernate.exception.ConstraintViolationException expected) {
            assertTrue(true);
        }
    }

    private Builder<?> and(Builder<?> builder) {
        return builder;
    }

    private Matcher<Iterable<? extends Product>> containsProducts(Matcher<Product>... productMatchers) {
        return containsInAnyOrder(productMatchers);
    }

    private Matcher<Iterable<Product>> hasSize(Matcher<? super Integer> sizeMatcher) {
        return iterableWithSize(sizeMatcher);
    }

    private Matcher<Product> productNamed(String name) {
        return new FeatureMatcher<Product, String>(equalTo(name), "a product named", "product name") {
            @Override protected String featureValueOf(Product actual) {
                return actual.getName();
            }
        };
    }
}
