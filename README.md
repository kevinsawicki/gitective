# gitective - Find the commits you're looking for

gitective is a Java library built on top of [JGit](http://www.eclipse.org/jgit) that makes investigating Git repositories simpler and easier.

## Examples

### Find the number of commits you authored but weren't the committer of
This case is common when doing peer code review or using a code review system.

```java
PersonIdent person = new Person("Michael Bluth", "michael@sitwell.com");
CommitCountFilter count = new CommitCountFilter();
AndCommitFilter filters = new AndCommitFilter();
filters.add(new AuthorFilter(person));
filters.add(new CommitterFilter(person).negate());
filters.add(count);
CommitService service = new CommitService("/repos/myrepo/.git"); 
service.walkFromHead(filters);
System.out.println(count.getCount()); //Prints the number of commits
```

### Find everyone that has authored commits that were 10-way merges
This example may seem uncommon but it will return 6 different users when run against the [linux kernel repository](http://git.kernel.org/?p=linux/kernel/git/torvalds/linux-2.6.git;a=summary).

```java
CommitService service = new CommitService("/repos/linux-2.6/.git"); 
AuthorSetFilter authors = new AuthorSetFilter();
AndCommitFilter filters = new AndCommitFilter();
filters.add(new ParentCountFilter(10));
filters.add(authors);
service.walkFromHead(filters);
for (PersonIdent author : authors.getPersons())
     System.out.println(author);
```

### Find the number of number of commits that occurred in master since a branch was created
This example assumes two current branches,  _master_ and a  _release1_ branch that was created from master some time ago. Both branches have had subsequent commits since the _release1_ branch was created.

```java
CommitService service = new CommitService("/repos/productA/.git");
RevCommit base = service.getBase("master", "release1");
CommitCountFilter count = new CommitCountFilter();
service.walkBetween("master", base, count);
System.out.println("Commits in master since release1 branch created: " + count.getCount());
count = new CommitCountFilter();
service.walkBetween("release1", base, count);
System.out.println("Commits in release1 since branched from master: " + count.getCount());
```

### What fraction of commits have a Gerrit Change-Id?
This example finds the number of commits in a repository that contain a [Gerrit](http://code.google.com/p/gerrit/) Change-Id entry in the commit message.

```java
CommitService service = new CommitService("/repos/egit/.git");
CommitCountFilter all = new CommitCountFilter();
CommitCountFilter gerrit = new CommitCountFilter();
AllCommitFilter filters = new AllCommitFilter();
filters.add(new AndCommitFilter().add(new ChangeIdFilter()).add(gerrit));
filters.add(all);
service.walkFromHead(filters);
System.out.println(MessageFormat.format(
     "{0} out of {1} commits have Gerrit change ids",
     gerrit.getCount(),	all.getCount()));
```

## Dependencies

JGit 1.0+

## License

[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html)