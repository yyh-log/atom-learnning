#### 39、组合总和
[组合总和](https://leetcode-cn.com/problems/combination-sum/) 
![](assets/5616a6cb.png)
```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> response = new ArrayList<>();
        if(target<=0){
            return response;
        }
        track(candidates,target,0,new ArrayList<>(),response);
        return response; 
    }

  void track(int[] a,int current,int index,List<Integer> tmp,List<List<Integer>> result){
   if(current==0){//满足条件
      result.add(new ArrayList<>(tmp));
	  return;
   }
  if(current<0) return;//不满足条件递归终止
  //每次循环都要从上一层传来的index开始，否则会造成重复
   for(int i=index;i<a.length;i++){
    tmp.add(a[i]);
	  track(a,current-a[i],i,tmp,result);
	  tmp.remove(tmp.size()-1);//回归状态
   }
}
}
```
#### 40、组合总和II
[组合总和II](https://leetcode-cn.com/problems/combination-sum-ii/)  
和上一题的区别是： 输入数组可以存在重复的元素，而每个数字在每个组合中只能使用一次，这两个要求都和上一题相反。    
要解决此问题需要考虑两点：  
因为元素不能重复选择，所以每次递归到下一层，index要加1；  
因为输入数组存在相同的元素，所以必须想要排序，然后在循环时判断a[i] == a[i - 1],如果相等就continue；如果不排序的话会出现如下错误：  
输入[1,5,1,4],目标值为6，那么如果不加第二步的限制的话会出现如下结果：  
[1,5][5,1]。如果先排序成[1,1,4,5]然后加a[i] == a[i - 1]判断就能避免重复的情况。 
```java
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> response = new ArrayList<>();
        if(target<=0){
            return response;
        }
        //candidates = Arrays.stream(candidates).distinct().toArray();
        Arrays.sort(candidates);
        track(candidates,target,0,new ArrayList<>(),response);
        //response = response.stream().distinct().collect(Collectors.toList());
        return response;
    }

    void track(int[] a,int current,int index,List<Integer> tmp,List<List<Integer>> result){
       if(current==0){//满足条件
           result.add(new ArrayList<>(tmp));
	       return;
        }
       //每次循环都要从上一层传来的index开始，否则会造成重复
       for(int i=index;i<a.length;i++){
        if(current-a[i]<0) break;//不满足条件递归终止
        // 小剪枝
        if (i > index && a[i] == a[i - 1]) {
                continue;
        }
         tmp.add(a[i]);
	     track(a,current-a[i],i+1,tmp,result);
	     tmp.remove(tmp.size()-1);//回归状态
       }
   }
}
```
#### 46、全排列
[全排列](https://leetcode-cn.com/problems/permutations/submissions/)  
![](assets/d587fa92.png)  
全排列有一个特点就是[1,2,3]和[2,1,3]虽然数字都相同，因为顺序不同，所以在全排列问题是属于不同的结果。如果第一个元素选择2，下一个元素是可以选择1的，所以用回溯法循环时i要从0开始，而不是常见的从父节点传下来的index或index+1开始，这是要非常注意的一点。又因为递归函数的循环i每次都是从0开始的，所以使用一个boolean[] visit数组来记录已经访问过的节点下标，一旦在循环判断i下标的节点已经访问过了，要用continue跳过该节点。  
```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if(nums.length==0) return result;
        boolean[] visit = new boolean[nums.length];
        track(nums,visit,new ArrayList<>(),result);
        return result;
    }

    public void track(int[] nums,boolean[] visit,List<Integer> tmp,List<List<Integer>> result){
        if(tmp.size()==nums.length){
            result.add(new ArrayList<>(tmp));
            return;
        }
        for(int i=0;i<nums.length;i++){
            if(visit[i]){
                continue;
            }
            tmp.add(nums[i]);
            visit[i] = true;//标注i下标已访问
            track(nums,visit,tmp,result);
            tmp.remove(tmp.size()-1);
            visit[i] = false;//回归状态
        }
    }

}
```
#### 47、全排列II
[全排列II](https://leetcode-cn.com/problems/permutations-ii/)  
![](assets/24cced41.png)  
和46题对比只有一个不同点在于输入数组存在重复的元素，所以为了避免得到带重复的结果，同一层值相同的节点只能选择一次，如图灰色的节点。所以程序中可以利用一个map以节点的值为key来记录同一层值相等的节点是否已经访问过，如果访问过则跳过。  
```java
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if(nums.length==0) return result;
        Arrays.sort(nums);//排序下可以更好理解为什么同一层相同的节点只能选择一次
        boolean[] visit = new boolean[nums.length];
        track(nums,visit,new ArrayList<>(),result);
        return result;
    }

    public void track(int[] nums,boolean[] visit,List<Integer> tmp,List<List<Integer>> result){
        if(tmp.size()==nums.length){//满足结果则添加到result中，然后结束递归
            result.add(new ArrayList<>(tmp));
            return;
        }
        Map<Integer,Boolean> visit2 = new HashMap<>();
        for(int i=0;i<nums.length;i++){
            if(visit[i]) continue;//已经访问过的节点忽略
            if(visit2.containsKey(nums[i])&&visit2.get(nums[i])) continue;//判断同一层值相同的点是否访问过，如果访问过则忽略
            visit2.put(nums[i],true);
            tmp.add(nums[i]);
            visit[i]=true;
            track(nums,visit,tmp,result);
            tmp.remove(tmp.size()-1);
            visit[i]=false;
        }
    }
}
```

####  78、 子集
[子集](https://leetcode-cn.com/problems/subsets/)   
回溯过程中各个变量的实时值 
![](assets/eea4ab53.png)
```java
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(0, nums, res, new ArrayList<Integer>());
        return res;
    }
    private void backtrack(int i, int[] nums, List<List<Integer>> res, ArrayList<Integer> tmp) {
        res.add(new ArrayList<>(tmp));
        for (int j = i; j < nums.length; j++) {
            tmp.add(nums[j]);
            backtrack(j + 1, nums, res, tmp);
            tmp.remove(tmp.size() - 1);
        }
    }
}
```
#### 90、 子集II
[子集II](https://leetcode-cn.com/problems/subsets-ii/)  
和78题相比区别在于输入数组存在相同的元素，为了避免得到重复的结果必须先排序，然后保证同一层相同值得节点只能被选择一次。  
```java
class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);//此题为了避免得到重复的结果必须先排序
        track(nums,0,new ArrayList<>(),result);
        return result;
    }

    public void track(int[] nums,int index,List<Integer> tmp,List<List<Integer>> result){
        result.add(new ArrayList<>(tmp));
        //Map<Integer,Boolean> visit = new HashMap<>();
        for(int i=index;i<nums.length;i++){
            //if(visit.containsKey(nums[i]) && visit.get(nums[i])) continue;
            if((i-index)>0 && nums[i]==nums[i-1]) continue;//同一层相同的值得节点只能访问一次，先排序在用此判断可以实现，也可以使用map记录已经访问过的节点
            //visit.put(nums[i],true);
            tmp.add(nums[i]);
            track(nums,i+1,tmp,result);
            tmp.remove(tmp.size()-1);
        }
    }
}
```
#### 110、平衡二叉树
[平衡二叉树](https://leetcode-cn.com/problems/balanced-binary-tree/)  
1、自底向上
```java
class Solution {
    public boolean isBalanced(TreeNode root) {
        return recur(root) != -1;
    }
    private int recur(TreeNode root) {
        if (root == null) return 0;
        int left = recur(root.left);
        if(left == -1) return -1;
        int right = recur(root.right);
        if(right == -1) return -1;
        return Math.abs(left - right) < 2 ? Math.max(left, right) + 1 : -1;
    }
}
复杂度分析：
时间复杂度O(N)： N 为树的节点数；最差情况下，需要递归遍历树的所有节点。
空间复杂度O(N)： 最差情况下（树退化为链表时），系统递归需要使用 O(N) 的栈空间

2、自顶向上
class Solution {
    public boolean isBalanced(TreeNode root) {
        if (root == null) return true;
        return Math.abs(depth(root.left) - depth(root.right)) <= 1 && isBalanced(root.left) && isBalanced(root.right);
    }

    private int depth(TreeNode root) {
        if (root == null) return 0;
        return Math.max(depth(root.left), depth(root.right)) + 1;
    }
}
复杂度分析：
时间复杂度 O(Nlog 2 N)： 最差情况下， isBalanced(root) 遍历树所有节点，占用 O(N) ；判断每个节点的最大高度 depth(root) 需要遍历 各子树的所有节点 ，子树的节点数的复杂度为 O(log_2 N)  
空间复杂度 O(N)： 最差情况下（树退化为链表时），系统递归需要使用O(N) 的栈空间。  
```
#### 116、填充每个节点的下一个右侧节点指针
[填充每个节点的下一个右侧节点指针](https://leetcode-cn.com/problems/populating-next-right-pointers-in-each-node/)  
按层次遍历树然后求解  
![](assets/4ea5230a.png)
```java
1）按层次遍历
class Solution {
    public Node connect(Node root) {
        if(root==null) return null;
        //按层次遍历树
        Queue<Node> queue = new LinkedList<>();//队列
        queue.add(root);
        while(queue.size()>0){
            int size = queue.size();//每一层的节点数量
            int i=0;
            while(i<size){
                Node node = queue.poll();//出队
                if(i<size-1){
                    node.next = queue.peek();
                }
                i++;
                if(node.left!=null) queue.add(node.left);//如果有左子树
                if(node.right!=null) queue.add(node.right);//如果有右子树
            }
        }
        return root;
    }
}
2）递归解法：  
适用递归解法最大的特点就是每一个节点执行的操作是一样的。  
class Solution {
   public void dfs(Node left, Node right) {
        if (left == null || right == null) return;
        left.next = right;
        dfs(left.left, left.right);
        dfs(right.left, right.right);
        dfs(left.right, right.left);
    }
    
    public Node connect(Node root) {
        if (root == null) return null;
        dfs(root.left, root.right);
        return root;
    }
}
```

#### 129、 求根到叶子节点数字之和  
[求根到叶子节点数字之和](https://leetcode-cn.com/problems/sum-root-to-leaf-numbers/)  
![](assets/b90129a9.png)  
```java
class Solution {
    int sum;
    public int sumNumbers(TreeNode root) {
        if(root==null) return 0;
        sum=0;
        caculate(root,0);
        return sum;
    }
    public void caculate(TreeNode node,int value){
        int current = value*10 + node.val;
        if(node.left==null&&node.right==null){
            sum+=current;
            return;
        }
        if(node.left!=null){
            caculate(node.left,current);
        }
        if(node.right!=null){
            caculate(node.right,current);
        }
    }
}
```
