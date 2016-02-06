Guidelines for Contributing
====
Contributions from the community are essential in keeping uPortal (any Open Source project really) strong and successful.  While we try to keep requirements for contributing to a minimum, there are a few guidelines we ask that you mind.

## Individual Contributor License Agreement

uPortal is an Apereo project.  Apereo requires that contributions be through Individual Contributor License Agreements.  If you wish to make a contribution, you must complete an Individual Contributor License Agreement.  It is through this agreement that you are licensing your contribution to Apereo so that Apereo can then license it to others under uPortal's open source software license.

You can learn more about [Apereo licensing generally][], the [contributor licensing agreements specifically][], and get the actual [Individual Contributor License Agreement form][], as linked.

## Getting Started

If you are just getting started with Git, GitHub and/or contributing to uPortal via GitHub there are a few pre-requisite steps.

* Make sure you have a [Jasig JIRA account](https://issues.jasig.org)
* Make sure you have a [GitHub account](https://github.com/signup/free)
* [Fork](http://help.github.com/fork-a-repo) the uPortal repository.  As discussed in the linked page, this also includes:
    * [Set](https://help.github.com/articles/set-up-git) up your local git install
    * Clone your fork

## Communicate first and often

What contribution are you looking to make?  Whom will this benefit?  Would anyone like to know what you're doing? Can anyone help you design and scope your contribution?  Who can validate that it meets the need?

Your potential bugfix or improvement or new feature should be represented by a JIRA issue in the issue tracker.  (You'll also need this JIRA issue key in your topic branch name and in your commit messages, below.)

You should probably post on [uportal-dev@][] signaling your intentions and inviting early input and feedback.

Perhaps uPortal adopters on [uportal-user@][] will be interested in helping to validate requirements and design?

You may be able to find others to collaborate with for mutual benefit.



## Create the working (topic) branch
Create a "topic" branch on which you will work.  The convention is to name the branch using the JIRA issue key.  If there is not already a JIRA issue covering the work you want to do, create one.  Assuming you will be working from the master branch and working on the JIRA issue UP-123 : 

    git checkout -b UP-123 master


For a more in-depth description of the git workflow check out the
[uPortal Git Workflow](https://wiki.jasig.org/display/UPC/Git+Workflow+for+Non-Committers)


## Code

* Apply uPortal [code conventions][] and [architecture][].
* Write automated unit tests exercising existing code you are touching and exercising your new code.  If you are fixing a bug, first write a unit test demonstrating the bug so as to stave off future regression.

## Collaborate

Consider pushing your changes to a topic branch in your (public) fork of the repository continually as you work, so that others can see what you are doing and can engage with you on it.  Consider commenting on the JIRA issue and/or on the uportal-dev@ email thread letting folks know this branch is available for collaboration.

## Commit

* Make commits of logical units.
* Be sure to use the JIRA issue key in the commit message.  This is how JIRA will pick up the related commits and display them on the JIRA issue.
* Make sure you have added the necessary tests for your changes.
* Run _all_ the tests to assure nothing else was accidentally broken.

_Prior to committing, if you want to pull in the latest upstream changes  (highly appreciated btw), please use rebasing rather than merging.  Merging creates "merge commits" that really muck up the project timeline._

## Submit

* You submitted a [Contributor License Agreement][], right?  Seriously, Apereo cannot accept your contribution other than under a Contributor License Agreement.
* Push your changes to a topic branch in your fork of the repository.
* Initiate a [pull request](http://help.github.com/send-pull-requests/).  Name the Pull Request prefixed with the issue identifier (e.g., "UP-4148 Update contributing guidance").  Mention and link the JIRA issue in the first couple lines of your Pull Request description so it's very convenient for reviewers to get to the JIRA issue from your pull request.  Use the pull request as another opportunity to communicate effectively about what is being changed.  Can your change be shown in before and after screenshots?  Did you make interesting implementation decisions or tradeoffs?
* Update the JIRA issue, adding a comment including a link to the created pull request.  If the problem or improvement came to be better understood through implementation, update the description and add comments to communicate what was learned.  A release engineer will rely upon the JIRA to summarize release notes for the release including your change and those release notes will link to the JIRA issue, so the JIRA issue as a good place to communicate clearly about what was changed why and how.

[Apereo licensing generally]: http://www.apereo.org/licensing
[contributor licensing agreements specifically]: http://www.apereo.org/licensing/agreements
[Contributor License Agreement]: http://www.apereo.org/licensing/agreements
[Individual Contributor License Agreement form]: http://www.apereo.org/sites/default/files/licensing/apereo-icla.pdf

[uportal-dev@]: https://wiki.jasig.org/display/JSG/uportal-dev
[uportal-user@]: https://wiki.jasig.org/display/JSG/uportal-user

[code conventions]: https://wiki.jasig.org/display/UPM41/Code+Styles+and+Conventions
[architecture]: https://wiki.jasig.org/pages/viewpage.action?pageId=65274379
