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
for (PersonIdent author : authors.getPerson())
     System.out.println(author.getName() + " <"+author.getEmailAddress()+">");
```

## Dependencies

JGit 1.0+

## License

[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html}