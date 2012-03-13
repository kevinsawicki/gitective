# gitective - Find the commits you're looking for

gitective is a Java library built on top of [JGit](http://www.eclipse.org/jgit)
that makes investigating Git repositories simpler and easier.  Gitective makes
it straight-forward to find interesting commits in a Git repository through
combining the included filters.

This library is available from [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.gitective%22%20AND%20a%3A%22gitective-core%22)

```xml
<dependency>
  <groupId>org.gitective</groupId>
  <artifactId>gitective-core</artifactId>
  <version>0.9.8</version>
</dependency>
```

Javadocs are available [here](http://gitective.org/apidocs/index.html).

# Details

gitective supports finding commits through registering filters that first
select commits and then run matchers against those commits.

The included filters can be used interchangebly for matching and selecting
commits.  This allows you to collect data on all commits visited as well as the
commits that match filters.

Suppose you want to find all the commits that fix bugs to Java source files. But
you also want to know the total number of commits that fix bugs so you can track
what subset of all the fixes are fixes to Java source files.

You would find this using the following steps:

  1.  Create a filter that selects commits that reference a bug in the message.
  2.  Create a filter that selects commits that alter a ```.java``` file.
  3.  Create a filter that counts the number of commits that are selected.

```java
CommitCountFilter bugCommits = new CommitCountFilter();
CommitCountFilter javaBugCommits = new CommitCountFilter();
CommitFinder finder = new CommitFinder("/repos/myrepo/.git");

finder.setFilter(new AndCommitFilter(new BugFilter(), bugCommits));
finder.setFilter(PathFilterUtils.andSuffix(".java"));
finder.setMatcher(javaBugCommits);
finder.find();

System.out.println(javaBugCommits.getCount() + " java bugs fixed");
System.out.println(bugCommits.getCount() + " total bugs fixed");
```

## Why would you use gitective?

  * You are new to Git and/or JGit and need to write an application that uses revision history.  Gitective aims to be simple enough to not require a deep understanding of Git or JGit in order to construct a filter to locate the commits you are looking for in a Git Repository.

  * You need to write an application that searches for commit information from multiple Git repositories.  Gitective supports re-using filters across repositories as well as multiple commit searches of the same repository.

  * You want to generate stats or reports based on the commit activity in Git repositories and are looking to use Java/JGit and want a head-start in writing the code that finds the commits needed.

## Commit Examples
Shown below are several examples of using the gitective filters and commit service classes, more examples can be found in the [unit tests](https://github.com/kevinsawicki/gitective/tree/master/org.gitective.core/src/test/java/org/gitective/tests).

### Get the HEAD commit in a repository

```java
Repository repo = new FileRepository("/repos/myrepo/.git");
RevCommit latestCommit = CommitUtils.getHead(repo);
System.out.println("HEAD commit is " + latestCommit.name());
```

### Find the number of commits you authored but weren't the committer of
This case is common when doing peer code review or using a code review system.

```java
PersonIdent person = new PersonIdent("Michael Bluth", "michael@sitwell.com");
CommitCountFilter count = new CommitCountFilter();
AndCommitFilter filters = new AndCommitFilter();

filters.add(new AuthorFilter(person));
filters.add(new CommitterFilter(person).negate());
filters.add(count);

CommitFinder finder = new CommitFinder("/repos/myrepo/.git");
finder.setFilter(filters).find();
System.out.println(count.getCount());
```

### Find everyone who has authored commits that were 10-way merges
This example may seem uncommon but it will return 6 different users when run against the [linux kernel repository](http://git.kernel.org/?p=linux/kernel/git/torvalds/linux-2.6.git;a=summary).

```java
AuthorSetFilter authors = new AuthorSetFilter();
AndCommitFilter filters = new AndCommitFilter();
filters.add(new ParentCountFilter(10), authors);

CommitFinder finder = new CommitFinder("/repos/linux-2.6/.git");
finder.setFilter(filters).find();

for (PersonIdent author : authors.getPersons())
     System.out.println(author);
```

### Find the number of commits that occurred in master since a branch was created
This example assumes two current branches,  _master_ and a  _release1_ branch that was created from master some time ago. Both branches have had subsequent commits since the _release1_ branch was created.

```java
Repository repo = new FileRepository("/repos/productA/.git");
RevCommit base = CommitUtils.getBase(repo, "master", "release1");

CommitCountFilter count = new CommitCountFilter();
CommitFinder finder = new CommitFinder(repo).setFilter(count);

finder.findBetween("master", base);
System.out.println("Commits in master since release1 was branched: " + count.getCount());

count.reset();
finder.findBetween("release1", base);
System.out.println("Commits in release1 since branched from master: " + count.getCount());
```

### What fraction of commits have a Gerrit Change-Id?
This example finds the number of commits in a repository that contain a [Gerrit](http://code.google.com/p/gerrit/) Change-Id entry in the commit message.

```java
CommitCountFilter all = new CommitCountFilter();
CommitCountFilter gerrit = new CommitCountFilter();
AllCommitFilter filters = new AllCommitFilter();
filters.add(new AndCommitFilter(new ChangeIdFilter(), gerrit), all);

CommitFinder finder = new CommitFinder("/repos/egit/.git");
finder.setFilter(filters).find();

System.out.println(MessageFormat.format(
     "{0} out of {1} commits have Gerrit change ids",
     gerrit.getCount(),	all.getCount()));
```

### Get commits in blocks of 100
This example collects commits into a list of a configured size and iteratively processes subsequent commit blocks.

```java
CommitListFilter block = new CommitListFilter();
CommitFilter limit = new CommitLimitFilter(100).setStop(true);
AndCommitFilter filters = new AndCommitFilter(limit, block);
CommitCursorFilter cursor = new CommitCursorFilter(filters);

Repository repo = new FileRepository("/repos/jgit/.git");
CommitFinder finder = new CommitFinder(repo);
finder.setFilter(cursor);

RevCommit commit = CommitUtils.getHead(repo);
while (commit != null) {
     finder.findFrom(commit);

     // block filter now contains a new block of commits

     commit = cursor.getLast();
     cursor.reset();
}
```

### Find commits referencing bugs
This example collects all the commits that have a 'Bug: XXXXXXX' line in the commit message.

```java
CommitListFilter commits = new CommitListFilter();
AndCommitFilter filter = new AndCommitFilter(new BugFilter(), commits);

CommitFinder finder = new CommitFinder("/repos/jgit/.git");
finder.setFilter(filter).find();
```

### Find files modified during a merge
This example visits all the files that were modified as part of a merge.

```java
CommitDiffFilter diffs = new CommitDiffFilter() {

     protected boolean include(RevCommit commit, Collection<DiffEntry> diffs) {
          // Diffs collection contains all files modified during merge
     }

};
AndCommitFilter filter = new AndCommitFilter(new ParentCountFilter(), diff);

CommitFinder finder = new CommitFinder("/repos/jgit/.git");
finder.setFilter(filter).find();
```

### Inspect notes associated with commits
This example visits all Git notes associated with each commit visited.

```java
NoteContentFilter notes = new NoteContentFilter() {

     protected boolean include(RevCommit commit, Note note, String content) {
          // Content string contains text of note associated with commit
     }

};

CommitFinder finder = new CommitFinder("/repos/jgit/.git");
finder.setFilter(notes).find();
```

### Generate commit histogram
This examples prints out how many commits happened each year in August.

```java
AuthorHistogramFilter filter = new AuthorHistogramFilter();
CommitFinder finder = new CommitFinder("/repos/redis/.git");
finder.setFilter(filter).find();

UserCommitActivity[] activity = filter.getHistogram().getUserActivity();
CommitCalendar commits = new CommitCalendar(activity);

for(YearCommitActivity year : commits.getYears())
     System.out.println(year.getMonthCount(Month.AUGUST) 
                          + " commits in August, " + year.getYear());
```

## Blob Examples

### Get content of file in HEAD commit

```java
Repository repo = new FileRepository("/repos/jgit/.git");
String content = BlobUtils.getHeadContent(repo, "src/Buffer.java");
```

### Diff two files

```java
Repository repo = new FileRepository("/repos/jgit/.git");

ObjectId current = BlobUtils.getId(repo, "master", "Main.java");
ObjectId previous = BlobUtils.getId(repo, "master~1", "Main.java");

Collection<Edit> edit = BlobUtils.diff(repo, previous, current);
```

## Building from source
gitective can be built using [Maven](http://maven.apache.org/). The pom.xml to build the core plug-in is located at the root of the org.gitective.core folder.

```
cd gitective/org.gitective.core
mvn clean install
```

## Dependencies

JGit 1.0+

## License

[MIT License](http://www.opensource.org/licenses/mit-license.php)

## Contributors

* [Kevin Sawicki](https://github.com/kevinsawicki) :: [contributions](https://github.com/kevinsawicki/gitective/commits?author=kevinsawicki)
* [Mykola Nikishov](https://github.com/manandbytes) :: [contributions](https://github.com/kevinsawicki/gitective/commits?author=manandbytes)
* [James Moger](https://github.com/gitblit) :: [contributions](https://github.com/kevinsawicki/gitective/commits?author=gitblit)
