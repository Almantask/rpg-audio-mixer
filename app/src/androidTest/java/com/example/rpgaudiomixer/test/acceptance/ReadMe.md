# Instructions

Testing android apps requires faking some of the components that may be blocking / slow / impossible to assert.
`ComposeRule` is used to pre-configure an activity to be launched.

In other words, for every activity (window/screen) on a mobile app - create a new compose rule, such as:

```kotlin
@WithJunitRule
class OtherActivityComposeRule(private val fakeInfra: FakeInfra) {
    private val androidComposeRule: AndroidComposeTestRule<*, OtherActivity> = createAndroidComposeRule<OtherActivity>().also {
        PicoToHiltBridge.fakeDependency = fakeInfra
        // optional: fakeInfra.reset()
    }
    
    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(CucumberHiltRule())
        .around(androidComposeRule)

    val composeRule: AndroidComposeTestRule<*, MainActivity>
        get() = androidComposeRule
}

```

Please note that to make use of DI, you will also need to include the fake dependency in `PicoToHiltBridge`, as well create `FakeOtherModule` and expose the dependency there.

Summary:

1. `ComposeRule` injects fake dependencies in activities using Pico DI. This happens automatically via `Cucumber-Android` (no explicit dependencies mapping is is needed)
2. To avoid multithreading problems, there is a `PicoToHiltBridge` that allows thread-safe access to dependency
3. `FakeOtherModule` injects the same fake dependencies to hilt, so they can be accessed within `Activity` (Android windows)