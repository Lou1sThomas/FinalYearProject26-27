# FinalYearProject26-27

## BolusBuddy — AI-Powered Nutrition & Insulin Bolus Calculator for Type 1 Diabetics

---

## Table of Contents
- [Overview](#overview)
- [Motivation](#motivation)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Bolus Calculation](#bolus-calculation)
- [Roadmap](#roadmap)
- [Testing](#testing)
- [Acknowledgements](#acknowledgements)
- [Author](#author)

---

## Overview
BolusBuddy is an Android application that helps people with type 1 diabetes calculate insulin bolus doses at mealtimes. Users can either **scan a food barcode** to retrieve nutritional information, or **photograph a restaurant meal** so that an AI model can recognise the food and estimate its carbohydrate content. The app then combines this carbohydrate estimate with the user's personal insulin settings to suggest a bolus dose.

> ⚠️ **Disclaimer:** BolusBuddy is an academic project and is **not** a certified medical device. It must not be used to make real insulin dosing decisions.

## Motivation
People with type 1 diabetes must count carbohydrates and calculate insulin doses before almost every meal. This is:
- **Time-consuming** — reading labels, weighing food and doing mental arithmetic several times a day.
- **Error-prone** — carbohydrate estimation errors can lead to hypoglycaemia (low blood sugar) or hyperglycaemia (high blood sugar).
- **Especially difficult when eating out** — restaurant meals rarely come with nutritional labels.

BolusBuddy aims to reduce this burden by automating carbohydrate lookup and estimation, and by performing the bolus calculation consistently using the user's own settings.

## Key Features
- 📷 **Barcode scanning** — scan packaged food and fetch nutritional data from the Open Food Facts database.
- 🍽️ **Meal photo recognition** — photograph a meal and have a deep learning model identify the food and estimate carbohydrates.
- 💉 **Bolus calculator** — calculate a suggested insulin dose from carbohydrates, current blood glucose and personal ratios.
- ⚙️ **Personal settings** — configure insulin-to-carb ratio, insulin sensitivity (correction) factor and target blood glucose.
- ✏️ **Manual override** — users can always review and edit estimated carbohydrate values before calculating a dose.

## Tech Stack
| Area            | Technology |
|-----------------|------------|
| Android         | Kotlin |
| Backend         | Python + FastAPI |
| Database        | PostgreSQL |
| Infrastructure  | Docker + Tailscale |
| AI              | PyTorch (ResNet50 transfer learning) |
| Food Data       | Open Food Facts API |

## Bolus Calculation
BolusBuddy uses the standard bolus calculation approach:

```
Meal bolus       = Carbohydrates (g) ÷ Insulin-to-Carb Ratio (g/unit)

Correction bolus = (Current BG − Target BG) ÷ Insulin Sensitivity Factor

Total bolus      = Meal bolus + Correction bolus
```

All values are set by the user to match the settings agreed with their diabetes care team. The app always displays the inputs used so the result can be checked.

## Roadmap
- [x] Project setup and repository structure
- [ ] Barcode scanning with Open Food Facts integration
- [ ] Bolus calculator and user settings screen
- [ ] Train and evaluate the ResNet50 food recognition model
- [ ] Build the FastAPI backend and connect it to PostgreSQL
- [ ] Containerise services with Docker
- [ ] Connect the Android app to the backend over Tailscale
- [ ] User testing and evaluation

## Testing
- **Android:** unit tests for the bolus calculation logic and UI tests for core flows.
- **Backend:** API tests with `pytest`.
- **AI model:** evaluation on a held-out test set (accuracy, precision, recall, confusion matrix).

## Acknowledgements
- [Open Food Facts](https://world.openfoodfacts.org/) for their open food product database.
- [PyTorch](https://pytorch.org/) and torchvision for the pre-trained ResNet50 model.
- University of Limerick and my project supervisor.

## Author
**Louis Thomas** — University of Limerick — Final Year Project 2026/2027