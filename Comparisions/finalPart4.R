rm(list = ls())
options(warn = -1)
library(rpart)
library (e1071)
library(klaR)
library(caret)
library(class)
library(neuralnet)
library(adabag)
library(randomForest)
library(ipred)
library(ada)
library(gbm)
trainingData = read.table(url("http://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/breast-cancer-wisconsin.data"), header = FALSE, sep=",")
a <- trainingData == '?'
is.na(trainingData) <- a
trainingData <- na.omit(trainingData)
#removing factors
indx <- sapply(trainingData, is.factor)
trainingData[indx] <- lapply(trainingData[indx], function(x) as.numeric(as.character(x)))

maxs=apply(trainingData,MARGIN = 2,max)
mins=apply(trainingData,MARGIN = 2,min)
scaled <- as.data.frame(scale(trainingData, center = mins, scale = maxs - mins))
columns <- c("V1","V2","V3","V4","V5","V6","V7","V8","V11","V10")
#CrossValidation:
folds <- cut(seq(1,nrow(scaled)),breaks=10,labels=FALSE)
dtAcc=0;
rfAcc=0;
nnAcc=0;
dlAcc=0;
svmAcc=0;
percSum=0;
adbAcc=0;
gbmAcc=0;
lrAcc=0;
nbAcc=0;
baggAcc=0;
knnAcc=0;
rfPresum=0;
dtPrec=0;
rfPrec=0;
nnPrec=0;
dlPrec=0;
svmPrec=0;
pPrec=0;
rfPrec=0;
adbPrec=0;
gbmPrec=0;
lrPrec=0;
nbPrec=0;
baggPrec=0;
knnPrec=0;
sigmoid = function(x) {
  1 / (1 + exp(-x))
}
accuracy=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Decision Tree
  train_tree = rpart(V11~., data=traind, parms = list(split="information"), control = rpart.control(minsplit = 3,minbucket = 1, cp = 0.001, maxdepth = 30))
  testpred = predict(train_tree, newdata=testd)
  conf <- table(pred = round(testpred, digits=1), true = testd$V11)
  tab <- table(testpred, testd$V11)
  accuracy <- sum(testd$V11==round(testpred, digits = 1))/length(testpred)
  accuracy <- accuracy*100
  dtAcc = dtAcc+accuracy
  dtPrec = dtPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Perceptron
  myformula <- as.formula(paste0('V11 ~ ', paste(names(traind[!names(traind) %in% 'V11']),collapse = ' + ')))
  pmodel = neuralnet(myformula, data= traind,hidden = 0,linear.output = F, threshold = 10, act.fct = sigmoid,err.fct="ce")
  testSample <- subset(testd, select=columns)
  pred <- compute(pmodel, testSample)
  accuracy <- sum(round(pred$net.result)==testd$V11)/length(testd$V11)
  accuracy <-accuracy*100
  percSum = percSum + accuracy
  conf <- table(pred = round(pred$net.result), true = testd$V11)
  pPrec = pPrec+(diag(conf)*100/apply(conf,2,sum))
  #plot(pmodel)
  
  #neural net
  myformula <- as.formula(paste0('V11 ~ ', paste(names(traind[!names(traind) %in% 'V11']),collapse = ' + ')))
  nnmodel = neuralnet(myformula, data= traind,hidden = c(4,4,4,4),linear.output = F, threshold = 0.1)
  testSample <- subset(testd, select=columns)
  pred <- compute(nnmodel, testSample)
  accuracy <- sum(round(pred$net.result)==testd$V11)/length(testd$V11)
  accuracy <-accuracy*100
  nnAcc = nnAcc + accuracy
  conf <- table(pred = round(pred$net.result), true = testd$V11)
  nnPrec = nnPrec+(diag(conf)*100/apply(conf,2,sum))
  #plot(nnmodel)
  
  #DeepLearning
  myformula <- as.formula(paste0('V11 ~ ', paste(names(traind[!names(traind) %in% 'V11']),collapse = ' + ')))
  dlmodel = neuralnet(myformula, data= traind,hidden = c(10,10,10,10,10,10,10,10),linear.output = F, threshold = 0.1)
  testSample <- subset(testd, select=columns)
  pred <- compute(dlmodel, testSample)
  accuracy <- sum(round(pred$net.result)==testd$V11)/length(testd$V11)
  accuracy <-accuracy*100
  dlAcc = dlAcc + accuracy
  conf <- table(pred = round(pred$net.result), true = testd$V11)
  dlPrec = dlPrec+(diag(conf)*100/apply(conf,2,sum))
  
  
  #SVM Model
  svmModel <- svm(V11~., data = traind, kernel="radial", cost = 1, gamma = 0.000015) 
  SVMprediction <- predict(svmModel, testd)
  conf <- table(pred = round(SVMprediction+0.05, digits=1), true = testd$V11)
  accuracy <-sum(diag(conf))/length(testd$V11)
  accuracy <-accuracy*100
  svmAcc = svmAcc+accuracy
  conf <- table(pred = round(SVMprediction), true = testd$V11)
  svmPrec = svmPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Random Forest
  rfModel <- randomForest(as.factor(traind$V11)~., data=traind, importance=TRUE, proximity=TRUE, ntree=500)
  RFpred <- predict(rfModel,testd,type='response')
  predTable <- table(observed = testd$V11, predicted = RFpred)
  rfAccuracy <- sum(diag(predTable))/sum(predTable)
  rfAccuracy <-rfAccuracy*100
  rfPresum = rfPresum+(diag(rfModel$confusion)/apply(rfModel$confusion,2,sum))
  rfAcc=rfAcc+rfAccuracy
  conf <- table(pred = round(as.numeric(RFpred)), true = testd$V11)
  rfPrec = rfPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Ada boosting
  model <- ada(traind$V11 ~ ., data = traind, iter=20, nu=1, type="discrete")
  p=predict(model,testd)
  accuracy <- sum(testd$V11==p)/length(p)
  accuracy <- accuracy * 100
  adbAcc = adbAcc+accuracy
  conf <- table(pred = round(as.numeric(p)), true = testd$V11)
  adbPrec = adbPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Gradient Boosting
  gb = gbm.fit(traind[,1:(11-1)],traind[,11],n.trees=1,verbose = FALSE,shrinkage=0.5 ,bag.fraction = 0.3 ,interaction.depth = 2,n.minobsinnode = 1, distribution = "bernoulli")
  predicted <- predict(gb,testd[,1:(11-1)],n.trees=1)
  gbm_table <- table(testd[,11], predicted)
  accuracy <- (sum(diag(gbm_table)) / sum(gbm_table))*100.0
  gbmAcc = gbmAcc+accuracy
  conf <- table(pred = round(as.numeric(predicted)), true = testd$V11)
  gbmPrec = gbmPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Naive Bayesian
  model <- naiveBayes(as.factor(traind$V11) ~ ., data = traind) 
  pred <- predict(model, testd)
  tab <- table(pred, testd$V11)    
  accuracy <- sum(diag(tab))/sum(tab)
  accuracy <-accuracy*100
  nbAcc = nbAcc+ accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  nbPrec = nbPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Logistic Regression
  lrmodel <- glm(V11~.,data=traind,family=binomial(link="logit"))
  pred = predict(lrmodel, type="response", newdata=testd)
  accuracy <- sum(testd$V11==round(pred))/length(testd$V11)
  accuracy <- accuracy*100
  lrAcc = lrAcc + accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  lrPrec = lrPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #KNN Model
  pred <- knn(train=traind, test=testd,cl=traind$V11, k=300)
  accuracy = 100* sum(pred == testd$V11)/length(pred)
  knnAcc = knnAcc+ accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  knnPrec = knnPrec+(diag(conf)*100/apply(conf,2,sum))
  
  #Bagging
  bgmodel <- bagging(V11~., data=traind, coob=TRUE)
  bgpred <- predict(bgmodel, testd)
  result <- data.frame(actual = testd$V11, prediction = bgpred)
  accuracy <- sum(round(result$prediction)==testd$V11)/length(testd$V11)
  accuracy <- accuracy*100
  baggAcc = baggAcc+ accuracy
  conf <- table(pred = round(as.numeric(bgpred)), true = testd$V11)
  baggPrec = knnPrec+(diag(conf)*100/apply(conf,2,sum))
  
}
cat("Perceptron total accuracy", percSum/10,"\n")
cat("Perceptron total Precision", mean(pPrec)/10,"\n")
cat("Neural Net total accuracy", nnAcc/10,"\n")
cat("Neural Net total Precision", mean(nnPrec)/10,"\n")
cat("Decision treetotal accuracy", dtAcc/10,"\n")
cat("Decision treetotal Precision", mean(dtPrec)/10,"\n")
cat("Deep learning total accuracy", dlAcc/10,"\n")
cat("Deep learning total Precision", mean(dlPrec)/10,"\n")
cat("SVM total accuracy", svmAcc/10,"\n")
cat("SVM total Precision", mean(svmPrec)/10,"\n")
cat("Random forest total accuracy", rfAcc/10,"\n")
cat("Random forest total Precision", mean(rfPrec)/10,"\n")
cat("Ada boost total accuracy", adbAcc/10,"\n")
cat("Ada boost total Precision", mean(adbPrec)/10,"\n")
cat("Gradient boost total accuracy", gbmAcc/10,"\n")
cat("Gradient boost total Precision", mean(gbmPrec)/10,"\n")
cat("Naive bayesian total accuracy", nbAcc/10,"\n")
cat("Naive bayesian total Precision", mean(nbPrec)/10,"\n")
cat("Logistic Regression total accuracy", lrAcc/10,"\n")
cat("Logistic Regression total Precision", mean(lrPrec)/10,"\n")
cat("K-NN total accuracy", knnAcc/10,"\n")
cat("K-NN total Precision", mean(knnPrec)/10,"\n")
cat("Bagging total accuracy", baggAcc/10,"\n")
cat("Bagging total Precision", mean(baggPrec)/10,"\n")