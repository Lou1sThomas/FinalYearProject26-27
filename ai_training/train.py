import torch
import torchvision.transforms as transforms
from torchvision.datasets import ImageFolder
from torch.utils.data import DataLoader
import torch.nn as nn
import torch.optim as optim
from model import create_model
import matplotlib.pyplot as plt

# ── Device not from CPU from GPU if available ──
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Using device: {device}")

# ── PATHS ──
DATASET_PATH = "dataset/food-101/images"
MODEL_SAVE_PATH = "model.pth"

# ── SETTINGS ──
BATCH_SIZE = 32
EPOCHS = 10
LEARNING_RATE = 0.001
NUM_CLASSES = 101

# ── IMAGE TRANSFORMS ──
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])

# ── LOAD DATASET ──
print("Loading dataset...")
full_dataset = ImageFolder(root=DATASET_PATH, transform=transform)

# Split 80% train 20% validation
train_size = int(0.8 * len(full_dataset))
val_size = len(full_dataset) - train_size
train_dataset, val_dataset = torch.utils.data.random_split(
    full_dataset, [train_size, val_size]
)

train_loader = DataLoader(train_dataset, batch_size=BATCH_SIZE, shuffle=True, num_workers=0, pin_memory=True)
val_loader = DataLoader(val_dataset, batch_size=BATCH_SIZE, shuffle=False, num_workers=0, pin_memory=True)

print(f"Training images: {train_size}")
print(f"Validation images: {val_size}")
print(f"Classes: {len(full_dataset.classes)}")  

# ── LOAD MODEL ──
print("Loading model...")
model = create_model(num_classes=NUM_CLASSES)
model = model.to(device)

# ── TRAINING SETUP ──
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.fc.parameters(), lr=LEARNING_RATE)

# ── TRAINING LOOP ──
train_losses = []
val_accuracies = []

print("Starting training...")
for epoch in range(EPOCHS):
    model.train()
    running_loss = 0.0

    for i, (images, labels) in enumerate(train_loader):
        images = images.to(device)
        labels = labels.to(device)

        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
        running_loss += loss.item()

        if i % 50 == 0:
            print(f"Epoch {epoch+1}/{EPOCHS} | Batch {i}/{len(train_loader)} | Loss: {loss.item():.4f}")

    avg_loss = running_loss / len(train_loader)
    train_losses.append(avg_loss)

    # ── VALIDATION ──
    model.eval()
    correct = 0
    total = 0

    with torch.no_grad():
        for images, labels in val_loader:
            images = images.to(device)
            labels = labels.to(device)
            outputs = model(images)
            _, predicted = torch.max(outputs, 1)
            total += labels.size(0)
            correct += (predicted == labels).sum().item()

    accuracy = 100 * correct / total
    val_accuracies.append(accuracy)

    print(f"Epoch {epoch+1} complete | Loss: {avg_loss:.4f} | Accuracy: {accuracy:.2f}%")

# ── SAVE MODEL ──
torch.save(model.state_dict(), MODEL_SAVE_PATH)
print(f"Model saved to {MODEL_SAVE_PATH} ")

# ── PLOT RESULTS ──
plt.figure(figsize=(12, 4))

plt.subplot(1, 2, 1)
plt.plot(train_losses)
plt.title("Training Loss")
plt.xlabel("Epoch")
plt.ylabel("Loss")

plt.subplot(1, 2, 2)
plt.plot(val_accuracies)
plt.title("Validation Accuracy")
plt.xlabel("Epoch")
plt.ylabel("Accuracy %")

plt.tight_layout()
plt.savefig("training_results.png")
plt.show()
print("Training complete! ")