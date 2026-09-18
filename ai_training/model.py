import torch
import torchvision
import torch.nn as nn

def create_model(num_classes = 101):
    #Load pretrained ResNet50 
    model = model.resnet50(weights= "IMAGENETT1_V1")
    
    #Freeze all layers
    for param in model.parameters():
        param.requires_grad = False
        
    model.fc = nn.Linear(2048, num_classes)
    
    return model

#Test it works

if __name__ == "__main__":
    model = create_model()
    print("Model created successfully!")
    print(f"Output classes: 101")
    
    
    