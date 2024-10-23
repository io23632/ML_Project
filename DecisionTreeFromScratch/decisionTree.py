import pandas as pd
import numpy as np
from collections import Counter

## class node contains :
""" 
Each node of the decision tree needs the following information:
- feature used to split the tree 
- threshold value for information gain
- the left side of the tree 
- the right tree 
- value : if it is a leaf node (none if it is not a leaf node)
"""

class Node:
    def __init__(self, feature, threshold, left, right, *, value=None):
        self.feature = feature
        self.threshold = threshold
        self.left = left
        self.right = right
        self.value = value
        
    def is_leaf(self):
        return self.value is not None
        

class DecisionTrees:
    """ 
    Constructor of the decision tree will take in the following parameters: 
    - The min_sample_split : The minimum number of samples of a node needed to preform a split 
    - max_depth: The max depth of the tree
    - num_features : How many feaurres do you want to pass to the tree 
    """
    def __init__(self, min_sample_split=2, max_depth=100, num_features=None):
        # Inialise the root node to be none:
        self.root = None
    
        self.min_sample_split = min_sample_split
        self.max_depth = max_depth
        self.num_features = num_features
    
    
    # Fit Funtion:
    def fit(self, X, y):
        # Number of features:
        self.num_features = X.shape[1]
        # call the helper function grow tree:
        self.root = self.grow_tree(X, y)
    
    def grow_tree(self, X, y, depth=0):
    
        num_samples, num_features = np.shape(X) 
        # Extract the unique labels (target values)
        num_labels = np.unique(y)
        
        ## First check the stopping criteria
        
        # max_depth has been reached: 
        # are there more than one unique target labels 
        # if the number of samples is below the min samples  value
        if (depth >= self.max_depth or len(num_labels) == 1 or num_samples < self.min_sample_split):
            """ 
            If the above conditions are true then the node you return is a leaf node, 
            the value of this leaf node will be the most common class 
            """
            leaf_value = self.most_common(y)
            return Node(value=leaf_value)
        
        feature_indexs = np.random.choice(num_features, self.num_features, replace=False)
        
        # Find the best split 
        best_feature, best_threshold = self.best_split(X, y, feature_indexs)
        
        # Create Child Nodes:
        left_indexes, right_indexes = self.get_split(X[:, best_feature], best_threshold)
        
        left = self.grow_tree(X[left_indexes,:], y[left_indexes], depth + 1)
        right = self.grow_tree(X[right_indexes,:], y[right_indexes], depth + 1)
        return Node(best_feature, best_threshold, left, right)
        
        
        # Most common class:
    def most_common(y):
        counter = Counter(y)
        # Returns a count of the most common elements in the list and their count
        """ 
        Example :
        Counter('abracadabra').most_common(1)[0][0]
            
        Counter('abracadabra').most_common(1) == ['a', 5]
        Counter('abracadabra').most_common(1)[0][0] == 'a'
        """
        most_common = counter.most_common(1)[0][0]
        return most_common
            
        
    # find the best split
    
    """ 
    The best split function itterates over the all features and within those 
    feautres itterates over all threshold values to find the threshold value that gives 
    the best inormation gain
    
    
    """
    
    def best_split(self, X, y, feat_indexes):
        
        # Initially set the best gain to -1 (this will be updated as we itterate over our thresholds)
        best_gain = -1
        
        # Initalise The split index and the split threshold values returns   
        split_index, split_threshold = None, None
        
        for feature_index in feat_indexes:
            #iterate over every feauture
            X_column = X[:, feature_index]
            # Get the number of thresholds as the number of unique values in the X_colum
            threshold_values = np.unique(X_column)
            
            for threshold in threshold_values:
                # for each threshold value; get the information gain:
                gain = self.info_gain(X, y, threshold)
                if gain > best_gain:
                    best_gain = gain
                    split_index = feature_index
                    split_threshold = threshold
                    
        return split_index, split_threshold
                

        
        
        
        
    
    # Calcualte information gain:
    
    def info_gain(self, X_column, y, threshold):
        """ 
        Information gain = Entropy(parent) - weighted_average * Entropy(children)
        """
        # parent entropy:
        entropy_parent = self.entropy(y)
        
        # Children entropy:
        
        # Get the children :
        left_index, right_index = self.get_split(X_column, threshold)
        if len(left_index == 0) or len(right_index == 0):
            return 0
        
        # Calculated weighted entropy of children
        
        num_samples = len(y)
        num_samples_right = len(left_index)
        num_samples_left = len(right_index)
        entropy_left = self.entropy(num_samples_left)
        entropy_right = self.entropy(num_samples_right)
        children_entropy = (num_samples_left / num_samples * entropy_left) + (num_samples_right/num_samples * entropy_right)
        information_gain = entropy_parent - children_entropy
        
        return information_gain
        

    
    """ 
    E = -Sum(p(X)*log2(p(X)))
        p(X) = #x/n, where #x = number of occurances of a class and n is the toal number of the class  
    """ 
        
    def entropy(self, y):
        
       """ 
       np.bincount(y) will return the histogram of the number of occurances of each class in y
       e.g. if y = [1, 1, 2, 2, 3, 3, 3, 3]
       np.bincount(y) = [0, 2, 2, 4] 0 has appeared 0 tims, 1 has apperead 2 times etc.
       
       """
       hist = np.bincount(y)
       p_x = hist / len(y)
       
       for p in p_x:
           if p > 0:
               return -np.sum(p*np.log2(p))
           
    def get_split(self, X_column, split_threshold):
    
        left_index = np.argwhere(X_column <= split_threshold).flatten()
        right_index = np.argmax(X_column > split_threshold).flatten()
    
        return left_index, right_index
    
    def predict(self, X):
        for x in X:
            return np.array(self.traverse_tree(x, self.root))
        
    def traverse_tree(self, x, node):
        
        # base case if the node a leaf nodeL
        if node.is_leaf():
            return node.value
        
        # else traverse tree 
        if x[node.feature] <= node.threshold:
            return self.traverse_tree(x, node.left)
        return self.traverse_tree(x, node.right)
    

