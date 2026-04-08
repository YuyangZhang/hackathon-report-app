---
description: Generate backend unit tests, run tests, and calculate coverage
---

# Backend Unit Testing Workflow

## 1.  prepare JaCoCo coverage plugin
// turbo
cd backend && echo "applying jacoco plugin"

## 2.  Generate unit tests for backend code (excluding POJOs)
```bash
cd backend
echo "Scanning for Java classes with business logic (excluding POJOs)..."
echo "Found business logic classes (excluding POJOs in dto/model directories):"
find src/main/java -name "*.java" | grep -v "/dto/" | grep -v "/model/" | sort
```

## 3.  Run backend unit tests
// turbo
cd backend && ./gradlew test

## 4.  Generate test coverage report
// turbo
cd backend && ./gradlew jacocoTestReport

## 5.  Check test coverage results
```bash
cd backend
echo "Coverage report available at: build/reports/jacoco/test/html/index.html"
echo "Opening coverage report..."
if command -v start &> /dev/null; then
    start build/reports/jacoco/test/html/index.html
elif command -v open &> /dev/null; then
    open build/reports/jacoco/test/html/index.html
else
    echo "Please open build/reports/jacoco/test/html/index.html manually"
fi
```

## 6.  Verify test results and coverage
```bash
cd backend
echo "=== Test Summary ==="
./gradlew test --info | grep -E "(BUILD|FAILED|SUCCESS|PASSED|FAILED)"
echo ""
echo "=== Coverage Summary ==="
if [ -f "build/reports/jacoco/test/html/index.html" ]; then
    echo "Coverage report generated successfully"
    echo "Total coverage: $(grep -o 'Total.*%' build/reports/jacoco/test/html/index.html | head -1)"
else
    echo "Coverage report not found"
fi
```

## Notes:
- This workflow focuses on backend Java code only
- POJOs are excluded based on directory structure (dto/model directories)
- Classes in other directories are considered to have business logic
- Uses JaCoCo for coverage reporting
- Tests are run using Gradle with JUnit platform
- Coverage report is generated in HTML format
- Minimum coverage threshold is set to 70%
