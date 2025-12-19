package com.example.rpgaudiomixer.acceptance

import io.cucumber.junit.CucumberOptions

@CucumberOptions(
    features = ["features"], // maps to androidTest/assets/features
    glue = ["com.example.rpgaudiomixer.acceptance.steps"]
)
class CucumberTestSuite