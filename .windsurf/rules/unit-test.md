---
trigger: manual
---
# 单元测试规范
- 所有的单元测试必须使用 JUnit 5 和 Mockito。
- 遵循 AAA 模式（Arrange, Act, Assert）。
- 每一个 Service 类都必须有对应的测试类，且 Mock 掉所有的依赖项。
- 变量命名需清晰，测试方法名采用 `should_doSomething_when_condition` 的格式。
- 断言优先使用 AssertJ 的 `assertThat` 语法。
- 必须包含对边界值（如 null, 空字符串, 极值）的测试。
- 测试覆盖率需达到 80% 以上。
- 使用 @DisplayName 注解为测试方法提供有意义的描述。
- 测试类的包结构需与被测试类保持一致。
- 测试方法需使用 @Test 注解。
- 测试方法需使用 @BeforeEach 注解进行初始化。
- 测试方法需使用 @AfterEach 注解进行清理。
- 测试方法需使用 @Mock 注解进行 Mock。
- 测试方法需使用 @InjectMocks 注解进行依赖注入。

