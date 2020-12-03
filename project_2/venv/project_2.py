import numpy as np
import matplotlib.pyplot as plt
import matplotlib.image as mpimg

# Define training dataset path and file names
TRAINING_DATASET = {"directory": "Face dataset/Training/",
                    "filenames": ["subject01.normal.jpg", "subject02.normal.jpg",
                                  "subject03.normal.jpg", "subject07.normal.jpg",
                                  "subject10.normal.jpg", "subject11.normal.jpg",
                                  "subject14.normal.jpg", "subject15.normal.jpg"],
                    "numbers": 8
                    }
# Define testing dataset path and file names
TESTING_DATASET = {"directory": "Face dataset/Testing/",
                   "filenames": ["subject01.happy.jpg", "subject07.happy.jpg",
                                 "subject11.happy.jpg", "subject14.happy.jpg",
                                 "subject14.sad.jpg"],
                   "numbers": 5
                   }
# Define image dimensions
WIDTH = 195
HEIGHT = 231


def read_images(dataset: dict) -> np.ndarray:
    images = np.ndarray(shape=(HEIGHT * WIDTH, dataset.get("numbers")))
    directory = dataset.get("directory")
    for i, filename in enumerate(dataset.get("filenames")):
        img = mpimg.imread(directory + filename)
        # print(len(img[0]))
        ri = np.array(img).flatten()
        # print(len(ri))
        images[:, i] = ri
        plt.subplot(2, 4, i + 1)
        plt.imshow(img, cmap="gray")
    plt.show()
    return images


def calculate_mean_face(dataset: np.ndarray) -> np.ndarray:
    mean = dataset.mean(axis=1).reshape(HEIGHT * WIDTH, 1)
    mean_face_image = mean_face.reshape(HEIGHT, WIDTH)
    plt.imshow(mean_face_image, cmap="gray")
    plt.show()
    plt.imsave("mean_face.jpg", mean_face_image, cmap="gray")
    return mean


def save_image()

training_data = read_images(TRAINING_DATASET)
print(training_data)
testing_data = read_images(TESTING_DATASET)
print(testing_data)
mean_face = calculate_mean_face(training_data)


