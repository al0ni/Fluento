# Verification results
Date: 15 November 2026. Local integrated delivery.
Environment: Windows 11, Temurin JDK 21.0.13, Maven 4.0.0-rc-3, JUnit 5.11.4, JaCoCo 0.8.12.
Command: mvn -B -ntp clean verify -Dfluento.captureDir=docs/screenshots

- Tests: 913; failures/errors: 0; skipped: 0.
- Whole-project lines: 643/695 (92.5%).
- Whole-project branches: 263/310 (84.8%).
- No coverage exclusions; legacy demos and entry points are included.
- Screens are captures of actual Swing components driven by ApplicationFlowTest.
- Observer/listener failure messages are intentional test injections.
- No external benchmark, GitHub CI result or grade guarantee is claimed.

| Package | Lines | Branches |
|---|---|---|
| com.fluento.app | 131/143 (91.6%) | 32/32 (100.0%) |
| com.fluento.progress | 64/66 (97.0%) | 26/26 (100.0%) |
| com.fluento.lessons.builder | 18/19 (94.7%) | 10/12 (83.3%) |
| com.fluento.lessons.ui | 62/63 (98.4%) | 31/36 (86.1%) |
| com.fluento.session | 150/152 (98.7%) | 103/122 (84.4%) |
| com.fluento.lessons.demo | 0/26 (0.0%) | 0/14 (0.0%) |
| com.fluento.model | 14/14 (100.0%) | 9/10 (90.0%) |
| com.fluento.lessons.factory | 70/70 (100.0%) | 24/24 (100.0%) |
| com.fluento.lessons.catalog | 89/95 (93.7%) | 12/18 (66.7%) |
| com.fluento.review | 45/47 (95.7%) | 16/16 (100.0%) |
