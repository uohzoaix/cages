### Version 0.806 ###
## A distributed synchronization library for Zookeeper ##

Cages is a Java library of distributed synchronization primitives that uses the Apache ZooKeeper system. If you can run a ZooKeeper machine or cluster, then you can use Cages to synchronize and coordinate data access, data manipulation and data processing, configuration change and more esoteric things like cluster membership across multiple machines.

**Usage examples**

  * Cassandra is part of a new breed of fast database that can be easily scaled horizontally. Cassandra works according to a NoSQL model, which can provide different levels of consistency for individual read and write operations, but does not offer synchronization across multiple read and write operations. Where needed, Cages can be used to coordinate and synchronize sequences of reads and writes to contended data on Cassandra from multiple client machines, for example using classes such as ZkReadLock, ZkWriteLock and ZkMultiLock. For more information see http://ria101.wordpress.com/2010/05/12/locking-and-transactions-over-cassandra-using-cages/

  * Starburst is a new platform under development for creating horizontally scalable highly event-driven network application architectures that can underpin more interactive rich internet applications based on Comet, HTML 5 and platforms like Adobe Flash, Microsoft Silverlight and UNITY (currently it is being developed as the main platform for a new game, http://www.FightMyMonster.com/). Starburst runs as a symmetric cluster of machines, where cluster nodes manage membership using Cages primitives such ZkContributedKeySet class).

**Simple code example**

```
    void executeTrade(long lotId, long sellerId, long buyerId) {
        // In the following we need to hold write locks over both the seller and buyer's account balances
        // so they can be checked and updated correctly. We also want a lock over the lot, since the value
        // of lots owned might be used in conjunction with the bank balance by code considering the
        // total worth of the owner. Acquiring the required locks simultaneously using ZkMultiLock avoids
        // the possibility of accidental deadlock occurring between this client code and other client code
        // contending access to the same data / lock paths.
        ZkMultiLock mlock = new ZkMultiLock();
        mlock.addWriteLock("/Bank/accounts/" + sellerId);
        mlock.addWriteLock("/Bank/accounts/" + buyerId);
        mlock.addWriteLock("/Warehouse/" + lotId);
        mlock.acquire();
        try {
            // 1. check buyer has sufficient funds
            // 2. debit buyer's account
            // 3. credit seller's account
            // 4. change ownership of goods
        } finally {
             mlock.release();
        }
    }
```

**Current issues**

Cages is quite rough and ready, and far from complete. I made the decision to offer it as open source because those parts that are present are already useful and there isn't much else that does a comparable job. There are some known limitions and issues related to certain aspects of ZooKeeper, and we plan on submitting ZooKeeper patches to address those shortly. More information on these will be placed in the Wiki soon.

**Things for the future**

Here is a brief list of new features planned:

  * Example code in the Wiki

  * Many more simple but useful synchronization primitives e.g. ZkNameValuePair

  * Transactions for Cassandra

  * ZooKeeper cluster hashing for maximum scalability

  * Bug fixes as they are discovered