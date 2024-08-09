# Support Vector Machine (SVM) for Binary Classification

This repository contains the Octave code to train an SVM in Octave as a quadratic programming problem.
The model is trained on a dataset of 3D points, each of which is labeled as either positive or negative.
To train the model, the function [`qp`](https://docs.octave.org/v4.2.0/Quadratic-Programming.html) is used to solve the quadratic programming problem.

To run the code and save the output to a file, run the following command in the `src` folder

```bash
timestamp=$(date +%s) && mkdir -p ../history && octave main.m | tee ../history/train_$timestamp.txt 
```

Notes:

- Not working in Octave 8.0.0 and higher, use any version below 8.0.0.

## Training

- `1700384484: MaxIter = 200`
- `1700404840: MaxIter = 100`
- `1700409633: MaxIter = 1000`
- `1700428104: MaxIter = 50000`

### Best results

Training: 1700428104

Performance indicators (avg.):


| Set       | (%) | Accuracy | Recall | Specificity | Precision | F-score |
|-----------|-----|----------|--------|-------------|-----------|---------|
| **Train** |     |       80 |     88 |          72 |        76 |      81 |
| **Test**  |     |       79 |     87 |          70 |        74 |      80 |
