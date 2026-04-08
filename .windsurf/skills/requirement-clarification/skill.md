---
description: Refine ambiguous requirements into clear, actionable statements
---

# Requirement Clarification Skill

This skill helps you refine ambiguous or incomplete requirements into clear, actionable statements using the SMART framework.

**Input**: requirements.json in jira/**file name**/ folder , If omitted, check if it can be inferred from conversation context. If vague or ambiguous you MUST prompt for available changes.

## Process

### 1. Review gathered requirements for clarity
- Analyze each requirement for ambiguity, completeness, and specificity
- Identify vague terms, missing details, or unclear objectives
- Check for conflicting or overlapping requirements
- Assess feasibility and dependency relationships

### 2. Ask clarifying questions for ambiguous items
- Generate specific questions to resolve ambiguities
- Identify missing information needed for complete requirements
- Probe for business context and user needs
- Request specific metrics, thresholds, or acceptance criteria

### 3. Convert vague requirements into SMART statements
Transform requirements to be:
- **Specific**: Clear, precise, and unambiguous
- **Measurable**: Quantifiable outcomes and success criteria
- **Achievable**: Realistic and feasible within constraints
- **Relevant**: Aligned with business objectives and user needs
- **Time-bound**: Clear deadlines and timeframes

### 4. Identify and document assumptions and constraints
- List all assumptions made during clarification
- Document technical, business, and resource constraints
- Identify dependencies on other systems or teams
- Note regulatory or compliance requirements

## Output
- Generates a clarified requirements markdown file requirment_mardown.json in file folder jira/**file name**/ 
- Includes original requirements vs. clarified requirements comparison
- Documents all assumptions and constraints
- Provides SMART-formatted requirements ready for implementation

## Usage

Use this skill when you have:
- Raw requirements that need refinement
- Ambiguous stakeholder requests
- Vague business objectives that need specificity
- Requirements that lack measurable success criteria

The skill will transform unclear requirements into actionable, implementable specifications with clear success criteria and documented constraints.
