import torch
import torchvision.models as models
import torch.nn as nn

def create_model(num_classes=101):
    # Load pretrained ResNet50
    model = models.resnet50(weights='IMAGENET1K_V1')
    
    # Freeze all layers
    for param in model.parameters():
        param.requires_grad = False
    
    # Replace final layer for 101 food classes
    model.fc = nn.Linear(2048, num_classes)
    
    return model

if __name__ == "__main__":
    model = create_model()
    print("Model created successfully ")
    print("Output classes: 101")
    
    
    