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


# function for reading and displaying image
def read_images(dataset: dict) -> np.ndarray:
    images = np.ndarray(shape=(HEIGHT * WIDTH, dataset.get("numbers")))
    directory = dataset.get("directory")
    for i, filename in enumerate(dataset.get("filenames")):
        img = mpimg.imread(directory + filename)
        r_i = np.array(img).flatten()
        images[:, i] = r_i
        # plt.subplot(2, 4, i + 1)
        # plt.imshow(img, cmap="gray")
    # plt.show()
    return images


# function for calculating and displaying the mean face
def calculate_mean_face(dataset: np.ndarray) -> np.ndarray:
    mean = dataset.mean(axis=1).reshape(HEIGHT * WIDTH, 1)
    mean_face_image = mean.reshape(HEIGHT, WIDTH)
    # plt.imshow(mean_face_image, cmap="gray")
    # plt.show()
    save_image("mean_face.jpg", mean_face_image)
    return mean


# function for saving the image
def save_image(image_name: str, image: np.ndarray) -> None:
    plt.imsave(image_name, image, cmap="gray")
    return None


# Training
# read in training image and define M
m_training = TRAINING_DATASET.get("numbers")
training_data = read_images(TRAINING_DATASET)
# calculate mean face
mean_face = calculate_mean_face(training_data)
# calculate matrix A
matrix_A = training_data - mean_face
# calculate matrix L
matrix_L = np.dot(matrix_A.transpose(), matrix_A)
# find eigenvalues of L
eigenvalues, eigenvectors = np.linalg.eig(matrix_L)
# put eigenvectors of L into a single matrix V
matrix_V = eigenvectors
# calculate matrix U that contains M eigenfaces
matrix_U = np.dot(matrix_A, matrix_V)
# project each training face onto the face space to obtain its eigenface coefficients
omega = np.ndarray(shape=(m_training, m_training))
print("---The Eigenface coefficients of the training images:")
for i, current_image_name in enumerate(TRAINING_DATASET.get("filenames")):
    print(f"--Training with image {current_image_name}:")
    # calculate omega of test image
    omega_i = np.dot(matrix_U.transpose(), matrix_A[:, i])
    omega[i, :] = omega_i
    print(f"-The Eigenface coefficient of image \"{current_image_name}\" is:\n{omega_i}")
# generate and output the eigenfaces of training image
eigenfaces = np.dot(matrix_U, omega)
training_data_eigenface_directory = "eigenfaces/training/"
for i in range(len(eigenfaces.transpose())):
    eigenface = eigenfaces.transpose()[i].reshape(HEIGHT, WIDTH)
    save_image("eigenface_" + TRAINING_DATASET.get("filenames")[i], eigenface)

print()
# Recognition
# read in testing image
m_testing = TESTING_DATASET.get("numbers")
testing_data = read_images(TESTING_DATASET)
# subtract mean face
test_matrix_A = testing_data - mean_face
# calculate eigenface coefficients of testing image
print("---The Eigenface coefficients and recognition results of the testing images:")
count_correct = 0
for i, current_image_name in enumerate(TESTING_DATASET.get("filenames")):
    print(f"--Recognizing image {current_image_name}:")
    # calculate omega of test image
    test_omega_i = np.dot(matrix_U.transpose(), test_matrix_A[:, i])
    print(f"-The Eigenface coefficient of image \"{current_image_name}\" is:\n{test_omega_i}")
    # reconstruct the test image
    test_eigenface = np.dot(matrix_U, test_omega_i)
    # recognize the face using with smallest Euclidean distance
    min_distance = np.inf
    for j in range(m_training):
        distance = np.linalg.norm(test_omega_i - omega[j])
        if distance < min_distance:
            min_distance = distance
            prediction = TRAINING_DATASET.get("filenames")[j]
    print(f"-The recognition result of image {current_image_name} is {prediction} with a distance of {min_distance}")
    if prediction.split(".")[0] == current_image_name.split(".")[0]:
        print("-The recognition result is correct")
        count_correct += 1
    else:
        print("-The recognition result is wrong")
# calculate the recognition accuracy
print(f"---The recognition accuracy for the test dataset is {count_correct / m_testing * 100}%")
