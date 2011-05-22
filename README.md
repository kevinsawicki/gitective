# gitective - Find the commits you're looking for

gitective is a Java library built on top of [JGit](http://www.eclipse.org/jgit) that makes investigating Git repositories simpler and easier.

## Examples

### Find the number of commits you authored but weren't the committer of
This case is common when doing peer code review or using a code review system.

```java
PersonIdent person = new Person("Michael Bluth", "michael@sitwell.com");
CommitCountFilter count = new CommitCountFilter();
AndCommitFilter filter = new AndCommitFilter();
filter.add(new AuthorFilter(person));
filter.add(new CommitterFilter(person).negate());
filter.add(count);
CommitService service = new CommitService("/repos/myrepo/.git"); 
service.walkFromHead(filter);
System.out.println(count.getCount()); //Prints the number of commits
```

## Dependencies
JGit 1.0+

## License
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html}