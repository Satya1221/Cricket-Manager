# Cricket Manager 🏏

A modern, comprehensive cricket team, franchise, tournament, and live match scoring application built for Android using Kotlin and Jetpack Compose.

## Features

- **Franchise & Squad Management**:
  - 8 pre-configured professional franchises (Mumbai Strikers, Chennai Super Kings, Bangalore Royals, Kolkata Knights, Delhi Capitals, Gujarat Titans, Rajasthan Royals, Hyderabad Sunrisers) with authentic colors, stadiums, and squad rosters.
  - Custom team creation with custom colors, cities, and home grounds.
  - Full squad management: Playing XI selection, Captain (C) & Wicket-Keeper (WK) roles, skill meters (Batting, Bowling, Fielding), and recruitment of new players.
- **Match Simulation & Live Scoring Engine**:
  - Multiple match formats (5, 10, or 20 overs per innings).
  - Toss simulation (heads/tails, elect to bat or bowl).
  - Live ball-by-ball score tracking: runs (0, 1, 2, 3, 4, 6), extras (wides, no-balls, byes, leg-byes), and wickets (bowled, caught, LBW, run-out, stumped, hit-wicket).
  - Dynamic strike rotation (odd runs, end of over ends change).
  - Tactical coaching plans: Batting Mindset (Defensive, Balanced, Aggressive, Blitz) & Bowling Strategies (Attack Stumps, Yorkers, Bouncers, Defensive Wide).
  - Match simulation tools: Simulate Ball, Simulate Over, and Auto-Simulate Innings.
  - Live real-time commentary stream with visual event indicators.
  - Undo functionality for last ball.
- **Detailed Scorecard**:
  - Full Batting scorecard (Runs, Balls, 4s, 6s, Strike Rate, Dismissal descriptions).
  - Full Bowling scorecard (Overs, Maidens, Runs conceded, Wickets, Economy).
  - Extras and fall of wickets breakdown.
- **Premier Cricket League**:
  - Points table with Matches played, Won, Lost, Points, and Net Run Rate (NRR).
  - Top-4 Playoff qualification indicators.
  - Fixtures and schedule tracking.
- **Statistics & Leaderboards**:
  - Orange Cap: Top run-scorers ranking with averages, strike rates, and high scores.
  - Purple Cap: Top wicket-takers ranking with economy rates and bowling figures.
- **Offline Persistence**:
  - Powered by Room Database with reactive Kotlin Flows and KSP.

## Tech Stack

- **Platform**: Android
- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose & Material Design 3
- **Local Database**: Room 2.7.0 with Kotlin Symbol Processing (KSP)
- **Architecture**: MVVM with Repository pattern and Kotlin Coroutines & Flow

## Build validation

Every push to `main` and every pull request now runs the Android debug build in GitHub Actions using JDK 21, Android SDK 36, and Gradle 9.3.1.

## Local secrets

Create a local `.env` file with your real `GEMINI_API_KEY` when AI features are enabled. `.env` is ignored by Git and `.env.example` contains only a safe placeholder.
