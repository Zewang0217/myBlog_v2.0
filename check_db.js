const { MongoClient } = require('mongodb');

async function checkData() {
    const uri = 'mongodb://localhost:27017';
    const client = new MongoClient(uri);
    
    try {
        await client.connect();
        const database = client.db('myBlog');
        const articles = database.collection('articles');
        
        // 检查文章数据
        const articleData = await articles.find().limit(3).toArray();
        console.log('文章数据样本:');
        console.log(JSON.stringify(articleData, null, 2));
        
        // 检查状态字段的不同值
        const statusValues = await articles.distinct('status');
        console.log('\n状态字段值:');
        console.log(statusValues);
        
    } catch (error) {
        console.error('数据库连接错误:', error);
    } finally {
        await client.close();
    }
}

checkData();