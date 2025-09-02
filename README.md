# 📊 Interactive Pie Chart App (Jetpack Compose)

An Android application built with **Jetpack Compose** for visualizing **custom pie charts** based on user input. Users can enter chart titles, categories, and values or percentages. The app renders a professional, interactive pie chart with rich visuals and smooth user experience.

---

## ✨ Features

- ✅ Input chart title, unit, and category values
- 📌 Supports both **Value Mode** and **Percentage Mode**
- 🎨 Random unique color assignment per category
- 🖱️ Tap on pie sectors to select and highlight
- 💬 Labels showing value + percentage + category
- 📋 Auto-wrapping labels for long category names
- 📉 Chart drawn with Canvas API in Jetpack Compose
- 🎯 Sector selection updates legend info

---

## 🧱 Architecture

- Jetpack Compose UI
- Single-activity, multiple-screen architecture
- No third-party chart libraries

---


---

## 🚀 Getting Started

### Requirements

- Android Studio Giraffe or newer
- Kotlin 1.9+
- Compose Compiler 1.5+
- Min SDK: 24+

### How to Run

1. Clone the project:
   ```bash
   git clone https://github.com/HuYiming2023/Charts
   cd pie-chart-compose
   ```

2. Open the project in Android Studio

3. Run the app on a device or emulator

---

## 📝 Example Usage

1. Enter chart title, like: `Monthly Expenses`
2. Select input type: `Value` or `Percentage`
3. Add entries like:
    - Food: 200
    - Rent: 800
    - Transport: 100
4. Tap **Generate Chart** to view an interactive pie chart

---

## 🛠️ Future Improvements

- Improve label overlap handling
- Add click-to-toggle label visibility
- Add 3D pie charts and donut charts
- Animate sector rendering
- Achieve the chart effects with different data sources

---


