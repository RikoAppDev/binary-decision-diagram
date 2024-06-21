# DSA Assignment 2 - Binary Decision Diagram (BDD) 🌳

This project implements a Binary Decision Diagram (BDD) to represent Boolean functions. It includes three main functions: `BDD_create`, `BDD_create_with_best_order`, and `BDD_use`.

## Implemented Functions

### 1. `BDD *BDD_create(string bfunction, string order);`
- **Description**: Creates a reduced Binary Decision Diagram to represent a given Boolean function.
- **Parameters**:
    - `bfunction`: Boolean function as a string expression.
    - `order`: Order of variables.
- **Returns**: Pointer to the created BDD structure.

### 2. `BDD *BDD_create_with_best_order(string bfunction);`
- **Description**: Finds the best order of variables for the given Boolean function by exploring various orders.
- **Operation**: Calls `BDD_create` multiple times with different variable orders.
- **Returns**: Pointer to the smallest BDD found.

### 3. `string BDD_use(BDD *bdd, string input_values);`
- **Description**: Uses the created BDD for a specific combination of input values to obtain the Boolean function result.
- **Operation**: Traverses the BDD tree from root to leaf based on the input values.
- **Returns**: Result as '1' or '0', or a negative value in case of an error.

## Implementation Details
- **BDD Structure**: Implemented using classes for `BDD` and `Node`. The `BDD` class contains attributes like the root node, variable count, node counter, order, and Boolean function.
- **Node Structure**: Each node includes attributes such as list of parents, left and right child, Boolean function, and variables.
- **Reduction**: The `BDD_create` function performs BDD reduction during creation by utilizing a hash map to eliminate duplicate nodes and merging nodes with identical functionality.
- **Correctness**: Ensured through iterative calls to `BDD_use` for various input values.

## Testing
- **Automatic Testing**: Generates random Boolean functions, creates BDDs, and measures time complexity and reduction percentage. Results are stored in `avg_data.csv`.
- **Manual Testing**: Provides options to create BDD with best order, with reduction, or without reduction for a specified Boolean function. Outputs are saved in `bdd_print.txt`.

## Complexity
- **Time Complexity**: Reduction implementation using a hash table reduces time complexity from \(O(N^2)\) to \(O(N)\).
- **Space Complexity**: Space usage of a BDD is calculated as nodes count * sizeof(node) + sizeof(BDD attributes). Reduction of redundant nodes results in significant space savings.

## Conclusion
The implementation of Shannon decomposition and reduction using a hash table is effective, as evidenced by the significant reduction in time complexity and node count.

## Technical Documentation 📑
For detailed technical documentation, please check the [`Dokumentacia_DSA_P2.pdf`](https://github.com/RikoAppDev/binary-decision-diagram/blob/main/Dokumentacia_DSA_P2.pdf) file included in this repository.
