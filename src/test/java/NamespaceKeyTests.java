import org.easybot.statistic.NamespaceKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NamespaceKey 类的单元测试
 * Unit tests for NamespaceKey class
 */
class NamespaceKeyTest {

    /**
     * 测试构造函数1：完整字符串包含命名空间的情况
     * Test constructor with full string containing namespace
     */
    @Test
    @DisplayName("构造函数测试 - 包含命名空间的完整字符串")
    void testConstructorWithFullStringContainingNamespace() {
        NamespaceKey key = new NamespaceKey("minecraft:walk_one_cm");

        assertEquals("minecraft", key.getNamespace());
        assertEquals("walk_one_cm", key.getPath());
        assertTrue(key.isHasExplicitNamespace());
    }

    @Test
    @DisplayName("测试旧版本MinecraftStat的路径")
    void testOldMinecraftStatPath() {
        NamespaceKey key = new NamespaceKey("walkOneCm");
        assertEquals("minecraft", key.getNamespace());
        assertEquals("walkOneCm", key.getPath());
        assertFalse(key.isHasExplicitNamespace());
    }
    
    /**
     * 测试构造函数1：字符串不包含命名空间的情况
     * Test constructor with string without namespace
     */
    @Test
    @DisplayName("构造函数测试 - 不包含命名空间的字符串")
    void testConstructorWithFullStringWithoutNamespace() {
        NamespaceKey key = new NamespaceKey("walk_one_cm");

        assertEquals("minecraft", key.getNamespace()); // 应该使用默认命名空间
        assertEquals("walk_one_cm", key.getPath());
        assertFalse(key.isHasExplicitNamespace());
    }

    /**
     * 测试构造函数1：无效输入异常处理
     * Test constructor exception handling for invalid inputs
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("构造函数测试 - 空字符串或null输入异常")
    void testConstructorWithInvalidInputs(String input) {
        assertThrows(IllegalArgumentException.class, () -> new NamespaceKey(input));
    }

    /**
     * 测试构造函数1：null输入异常处理
     * Test constructor exception handling for null input
     */
    @Test
    @DisplayName("构造函数测试 - null输入异常")
    void testConstructorWithNullInput() {
        assertThrows(IllegalArgumentException.class, () -> new NamespaceKey(null));
    }

    /**
     * 测试构造函数2：显式指定命名空间和路径
     * Test constructor with explicit namespace and path
     */
    @Test
    @DisplayName("构造函数测试 - 显式指定命名空间和路径")
    void testConstructorWithExplicitNamespaceAndPath() {
        NamespaceKey key = new NamespaceKey("custom_ns", "custom_path");

        assertEquals("custom_ns", key.getNamespace());
        assertEquals("custom_path", key.getPath());
        assertTrue(key.isHasExplicitNamespace());
    }

    /**
     * 测试构造函数2：命名空间为空时使用默认值
     * Test constructor using default namespace when namespace is empty
     */
    @Test
    @DisplayName("构造函数测试 - 空命名空间使用默认值")
    void testConstructorWithEmptyNamespace() {
        NamespaceKey key = new NamespaceKey("", "custom_path");

        assertEquals("minecraft", key.getNamespace()); // 应该使用默认命名空间
        assertEquals("custom_path", key.getPath());
        assertTrue(key.isHasExplicitNamespace());
    }

    /**
     * 测试构造函数2：命名空间为null时使用默认值
     * Test constructor using default namespace when namespace is null
     */
    @Test
    @DisplayName("构造函数测试 - null命名空间使用默认值")
    void testConstructorWithNullNamespace() {
        NamespaceKey key = new NamespaceKey(null, "custom_path");

        assertEquals("minecraft", key.getNamespace()); // 应该使用默认命名空间
        assertEquals("custom_path", key.getPath());
        assertTrue(key.isHasExplicitNamespace());
    }

    /**
     * 测试toString方法
     * Test toString method
     */
    @Test
    @DisplayName("toString方法测试")
    void testToString() {
        NamespaceKey key1 = new NamespaceKey("test", "example");
        assertEquals("test:example", key1.toString());

        NamespaceKey key2 = new NamespaceKey("another:example");
        assertEquals("another:example", key2.toString());
    }

    /**
     * 测试toOriginalString方法 - 有显式命名空间
     * Test toOriginalString method with explicit namespace
     */
    @Test
    @DisplayName("toOriginalString方法测试 - 有显式命名空间")
    void testToOriginalStringWithExplicitNamespace() {
        NamespaceKey key = new NamespaceKey("test", "example");
        assertEquals("test:example", key.toOriginalString());
    }

    /**
     * 测试toOriginalString方法 - 无显式命名空间
     * Test toOriginalString method without explicit namespace
     */
    @Test
    @DisplayName("toOriginalString方法测试 - 无显式命名空间")
    void testToOriginalStringWithoutExplicitNamespace() {
        NamespaceKey key = new NamespaceKey("example");
        assertEquals("example", key.toOriginalString());
    }

    /**
     * 测试equals方法
     * Test equals method
     */
    @Test
    @DisplayName("equals方法测试")
    void testEquals() {
        NamespaceKey key1 = new NamespaceKey("test", "example");
        NamespaceKey key2 = new NamespaceKey("test:example");
        NamespaceKey key3 = new NamespaceKey("different", "example");
        NamespaceKey key4 = new NamespaceKey("test", "different");

        // 相等的对象
        assertEquals(key1, key2);

        // 不同的命名空间
        assertNotEquals(key1, key3);

        // 不同的路径
        assertNotEquals(key1, key4);
    }

    /**
     * 测试hashCode方法
     * Test hashCode method
     */
    @Test
    @DisplayName("hashCode方法测试")
    void testHashCode() {
        NamespaceKey key1 = new NamespaceKey("test", "example");
        NamespaceKey key2 = new NamespaceKey("test:example");
        NamespaceKey key3 = new NamespaceKey("different", "example");

        // 相等对象应该有相同的hashCode
        assertEquals(key1.hashCode(), key2.hashCode());

        // 不相等对象应该有不同的hashCode
        assertNotEquals(key1.hashCode(), key3.hashCode());
    }
}
