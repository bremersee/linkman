package org.bremersee.linkman.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.bremersee.linkman.model.Translation;
import org.junit.jupiter.api.Test;

/**
 * The link entity test.
 *
 * @author Christian Bremer
 */
class LinkEntityTest {

  /**
   * Gets id.
   */
  @SuppressWarnings({"SimplifiableJUnitAssertion", "ConstantConditions", "EqualsWithItself"})
  @Test
  void getId() {
    LinkEntity expected = new LinkEntity();
    String value = UUID.randomUUID().toString();
    expected.setId(value);
    assertNotNull(expected.getId());
    assertEquals(value, expected.getId());
    assertFalse(expected.equals(null));
    assertFalse(expected.equals(new Object()));
    assertTrue(expected.equals(expected));

    LinkEntity actual = new LinkEntity();
    actual.setId(expected.getId());
    assertTrue(actual.equals(expected));
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(expected.toString(), actual.toString());
  }

  /**
   * Gets href.
   */
  @SuppressWarnings("SimplifiableJUnitAssertion")
  @Test
  void getHref() {
    LinkEntity expected = new LinkEntity();
    String value = UUID.randomUUID().toString();
    expected.setHref(value);
    assertEquals(value, expected.getHref());
    assertEquals(value, expected.getHref());

    LinkEntity actual = new LinkEntity();
    actual.setHref(expected.getHref());
    assertTrue(actual.equals(expected));
    assertEquals(expected.hashCode(), actual.hashCode());
    assertEquals(expected.toString(), actual.toString());
  }

  /**
   * Gets blank.
   */
  @Test
  void getBlank() {
    LinkEntity expected = new LinkEntity();
    assertFalse(expected.getBlank());
    expected.setBlank(Boolean.TRUE);
    assertTrue(expected.getBlank());
  }

  /**
   * Gets text.
   */
  @Test
  void getText() {
    LinkEntity expected = new LinkEntity();
    String value = "Welcome";
    expected.setText(value);
    assertEquals(value, expected.getText());

    assertEquals(value, expected.getText(Locale.FRENCH));

    Set<Translation> translations = Collections.singleton(new Translation("de", "Willkommen"));
    expected.setTextTranslations(translations);
    assertEquals(translations, expected.getTextTranslations());

    assertEquals("Willkommen", expected.getText(Locale.GERMANY));
  }

  /**
   * Gets display text.
   */
  @Test
  void getDisplayText() {
    LinkEntity expected = new LinkEntity();
    assertTrue(expected.getDisplayText());

    expected.setDisplayText(null);
    assertTrue(expected.getDisplayText());

    expected.setDisplayText(false);
    assertFalse(expected.getDisplayText());
  }

  /**
   * Gets description.
   */
  @Test
  void getDescription() {
    LinkEntity expected = new LinkEntity();
    String value = "Welcome";
    expected.setDescription(value);
    assertEquals(value, expected.getDescription());

    assertEquals(value, expected.getDescription(Locale.FRENCH));

    Set<Translation> translations = Collections.singleton(new Translation("de", "Willkommen"));
    expected.setDescriptionTranslations(translations);
    assertEquals(translations, expected.getDescriptionTranslations());

    assertEquals("Willkommen", expected.getDescription(Locale.GERMANY));
  }

  /**
   * Gets card image url.
   */
  @Test
  void getCardImageUrl() {
    LinkEntity expected = new LinkEntity();
    String value = UUID.randomUUID().toString();
    expected.setCardImage(value);
    assertEquals(value, expected.getCardImage());
  }

  /**
   * Gets menu image url.
   */
  @Test
  void getMenuImageUrl() {
    LinkEntity expected = new LinkEntity();
    String value = UUID.randomUUID().toString();
    expected.setMenuImage(value);
    assertEquals(value, expected.getMenuImage());
  }

  /**
   * Compare to.
   */
  @Test
  void compareTo() {
    LinkEntity a = new LinkEntity();
    a.setText("Welcome");
    Set<Translation> at = Collections.singleton(new Translation("de", "Willkommen"));
    a.setTextTranslations(at);
    LinkEntity b = new LinkEntity();
    b.setText("Hello");
    Set<Translation> bt = Collections.singleton(new Translation("de", "Hallo"));
    b.setTextTranslations(bt);
    assertTrue(a.compareTo(b, Locale.GERMAN) > 0);

    a.setOrder(1);
    b.setOrder(2);
    assertTrue(a.compareTo(b) < 0);

    a = new LinkEntity();
    a.setOrder(1);
    b = new LinkEntity();
    b.setOrder(1);
    assertEquals(0, a.compareTo(b));
  }

}