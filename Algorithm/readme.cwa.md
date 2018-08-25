
# Openjudge-NOI题库-二维数组回形遍历

* 题目描述 Description
给定一个row行col列的整数数组array，要求从array[0][0]元素开始，按回形从外向内顺时针顺序遍历整个数组。如图所示：

![cwa](http://media.openjudge.cn/images/upload/1415002182.gif)

* 输入输出格式 Input/output
输入格式：
输入的第一行上有两个整数，依次为row和col。
余下有row行，每行包含col个整数，构成一个二维整数数组。
（注：输入的row和col保证0 < row < 100, 0 < col < 100）

输出格式：
按遍历顺序输出每个整数。每个整数占一行。

* 输入输出样例 Sample input/output
样例测试点#1

输入样例：
4 4
1 2 3 4
12 13 14 5
11 16 15 6
10 9 8 7

输出样例：
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16

