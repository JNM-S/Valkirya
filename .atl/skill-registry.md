# Skill Registry — Valkyria

Generated: 2026-03-31
Project: Valkyria
Stack: Android (Kotlin, Java, XML layouts, Material Design 3, Gradle KTS)

---

## User Skills

| Skill | Trigger |
|-------|---------|
| `branch-pr` | Creating a pull request, opening a PR, preparing changes for review |
| `issue-creation` | Creating a GitHub issue, reporting a bug, requesting a feature |
| `judgment-day` | "judgment day", "review adversarial", "dual review", "juzgar", "que lo juzguen" |
| `skill-creator` | Creating new AI skills, adding agent instructions, documenting patterns for AI |
| `go-testing` | Writing Go tests, using teatest, adding test coverage (N/A for this project) |
| `sdd-explore` | Orchestrator launches to investigate codebase or clarify requirements |
| `sdd-propose` | Orchestrator launches to create a change proposal |
| `sdd-spec` | Orchestrator launches to write specifications |
| `sdd-design` | Orchestrator launches to create technical design |
| `sdd-tasks` | Orchestrator launches to break down tasks |
| `sdd-apply` | Orchestrator launches to implement tasks |
| `sdd-verify` | Orchestrator launches to validate implementation |
| `sdd-archive` | Orchestrator launches to archive a completed change |

---

## Compact Rules

### Android / Kotlin Conventions (auto-detected)

```
[android-kotlin]
- Language: Kotlin (JVM 11), no Java new files
- Min SDK: 24 (Android 7.0), Target SDK: 36
- Architecture: Single-module Android app, Activity-based navigation (no Fragments, no Jetpack Compose)
- UI: XML layouts with Material Design 3 (Theme.Material3.DayNight.NoActionBar)
- Data persistence: SharedPreferences only (no Room, no DataStore yet)
- Dependencies: androidx.biometric, Material Components, ConstraintLayout, AppCompat
- Testing: JUnit4 (unit), Espresso (instrumented) — only scaffold tests exist
- Build: Gradle KTS (build.gradle.kts), version catalog (libs.versions.toml assumed)
- Naming: PascalCase for Activities (e.g. Baul_contrasenas), camelCase for variables — mixed style exists, prefer PascalCase for new Activities
- No CI/CD configured
- No linting config detected (.editorconfig, ktlint, detekt absent)
- Designs reference: Valkirya/Diseños/ (PNG mockups for all screens)
```

### SDD Rules

```
[sdd]
- Artifact store: engram
- Use Given/When/Then for spec scenarios
- Use RFC 2119 keywords (MUST, SHALL, SHOULD, MAY)
- Group tasks by phase: infrastructure, implementation, testing
- Hierarchical task numbering: 1.1, 1.2, etc.
- Include rollback plan for risky changes
```

---

## Project Conventions Files

No `AGENTS.md`, `CLAUDE.md`, `.cursorrules`, or `GEMINI.md` found at project root.
Global AGENTS.md at: `C:\Users\JUAN\.config\opencode\AGENTS.md`
