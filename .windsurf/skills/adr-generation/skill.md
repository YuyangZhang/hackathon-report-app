---
name: adr-generartion
description: Produce Architecture Decision Records documenting key technical choices
---

# ADR Generation Skill

This skill helps you produce Architecture Decision Records (ADRs) that document key technical choices and their rationale.

**Input**: Optionally specify a requirment file name. If omitted, check if it can be inferred from conversation context. If vague or ambiguous you MUST prompt for available changes.


## Process

### 1. Identify architectural decisions needed
- Analyze requirements for architectural implications
- Identify technical choices that will impact system design
- Recognize decisions affecting scalability, performance, maintainability
- Spot integration points and technology stack decisions
- Find decisions about data structures, APIs, and system boundaries

### 2. Evaluate alternative approaches
- Research and identify multiple viable solutions
- Compare pros and cons of each approach
- Assess trade-offs (performance vs. complexity, cost vs. benefit)
- Consider team expertise and learning curve
- Evaluate long-term maintenance and evolution implications

### 3. Document decision context, options, and outcomes
- **Context**: Background and problem statement
- **Options**: Detailed description of alternatives considered
- **Decision**: Which option was chosen and why
- **Consequences**: Positive and negative impacts of the decision
- **Status**: Current status (proposed, accepted, deprecated, etc.)

### 4. Create architectural decision records for significant technical choices
- Generate structured ADR documents
- Follow standard ADR format (MADR or similar)
- Include decision date, stakeholders, and rationale
- Document implementation notes and verification criteria
- Store ADRs in accessible repository for future reference

## Output
- Creates structured ADR markdown files in folder output
- Documents complete decision-making process
- Provides rationale for future reference and team alignment
- Generates ADR index for easy navigation
- output folder is jira/**file name**/

## Usage

Use this skill when you need to:
- Document significant technical decisions
- Provide rationale for architecture choices
- Create a record of decision-making for future teams
- Ensure architectural decisions are well-reasoned and communicated

The skill will help you create comprehensive ADRs that capture the full context and reasoning behind important technical decisions, ensuring architectural knowledge is preserved and shareable.
