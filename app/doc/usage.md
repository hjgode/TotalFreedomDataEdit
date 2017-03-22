# Totalfreedom data editing demo plugin with Regex support

When you use barcode scanning and want to change the form of the scanned barcode data before it is wedged into an application you need a Data Editing Plugin.
A Data Editing Plugin is a custom software that receives the scanned Barcode data and can return changed data.

Instead of writing different Data Editing Plugins using Android coding technologies, the provided Demo Data Edit Plugin provides a single way to use the same plugin to perform custom data changes. The custom data changes are driven by Java Regex, regular expressions to match and change data.
 
All incoming barcode data is compared to a pattern inside a list of rules. If the scanned data matches the pattern of a rule, the replacement part is used to change the data. The pattern and replacement string are evaluated using Java Regex and become very powerful.

A simple example is the following rule line:

    (.*)=>$1\n;

This will replace the scanned data by the data followed by a New Line character.

For example: to replace all FNC1 sysmbols inside a scanned barcode by the string <FNC1> use the following line:

    +=>\u001d=><fnc1>;

## Note

If no rule matches, nothing is returned and a 'bad scan' sound is emitted.

## Installation

Install the Demo Data Editing plugin apk either using ADB or by copying the apk file to the device and then use a file browser to install.

### Setup

Go to Settings-Scanning-Internal Scanner-Default Profile-Data Processing Settings-Data Editing Plugin and enter the name of the Totalfreedom Demo DataEdit class:

    hsm.demo.totalfreedom/.DataEdit

![Settings 1](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/DataProcessingSettings_01.png)

![Settings 2](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/DataProcessingSettings_02.png)

### Android Permissions

As the Plugin uses a shared directory to read the rules file, it needs android.permission.READ_EXTERNAL_STORAGE and android.permission.WRITE_EXTERNAL_STORAGE. Without these permissions the application code does not work. The write permissions is used to write a default, standard rules file as a starter.

![Android Permissions for TotalFreedomTest](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/AppPermissions_01.png)

### Usage - TotalFreedomTest

After the apk is installed there is also a Test application available called "TotalFreedomTest". The plugin itself does not need an application to run. The test application has an input field, that can be used to show the wedged data and a information area, that shows internals of the plugin work.

![TotalFreedomTest_01](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/TotalFreedomTest_01.png)

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/TotalFreedomTest_02.png)

# Configuration

The Demo Totalfreedom plugin is configured by a text rules file.

## Rules Editor

There is also the possibilty to edit rules with the built-in [Rules Editor](https://github.com/hjgode/TotalFreedomDataEdit/blob/master/app/doc/regex-editor.md)

## The rules file

The demo plugin tool uses the file
 
    <Device>\Internal storage\Documents\dataedit_regex.ini

* NOTE: The directory "Documents" might not exist on your device. If so, you have to manually create the directory.

The rule file can be copied from and to your PC:

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/storage_documents.png)

The list of rules is read on every scanned barcode. Any change will be applied on next barcode scan.

A sample file content will be:

    # this is a comment;
    test(.)(.)(.*)=>($1) $2-$3\n;
    ]A0=>(.*)=>$1\n;
    +g=>\u001D=>FNC1;
    (.*)=>$1\n;

### Comment line

A rule line starting with a # will be treated as comment and will not be further processed.

    # this is a comment;

### A rule line

Every rule line can have either two or three sections or fields:

	regex=>replacement

	Options/AimID=>regex=>replacement

Fields have to be separated by 

    =>

and the rule lines in a file have to end with:

    ;\r\n

(A semicolon followed by carriage return / line feed.)
 
#### Two-field rules

The first field defines the pattern that a barcode data must match for the rule being executed. The second field is the replacement to be used if the pattern matches the barcode data.

    regex pattern=>replacement;

#### Three-field rules

The first field may contain the option '+' and or 'g' and may be followed by the Symbology AimID that must match the AimID of the scanned barcode.

    [+][g][AimID]=>pattern=>replacement;

##### The '+' option

Rules are processed line by line from top to bottom. If a rule matches, the processing of further rule lines will not take place. If the '+' option is used, the rule processing will not stop after a rule line is matched.

##### The 'g' option

The demo plugin can match a pattern on the whole data or do a simple search-and-replace. If the 'g' option is used, the pattern and replacement field are used for a global search&replace.

* The 'g' option implies also as the 'no-stop' handling. A following rule will be processed.

##### The AimID

The standard AimID can be used to let the demo plugin match only against barcode that has the same AimID.

For example:

    ]A0=>(.*)=>Aim Id matches for: $1;

will output the data with the prefix "Aim Id matches: " only for barcode with AimID "]A0".

##### The regex

The regex field defines the pattern to be searched for in the input data.

##### The replacement

The replacement defines the output for scanned data if the regex pattern matches.

# To escape or not escape characters?

For Java strings itself and Java Regex the escape symbol '\' has a special meaning. Java escape sequences need to be used to describe a non-printable character or code.

## Java Escape sequences

Codes that need to be replaced by a Java escape sequence. The rule file has to follow the syntax for java strings.

    | Escape Sequence | Description                                                 |
    | --------------- | ----------------------------------------------------------- |
    | \t              | Inserts a tab in the text at this point. 					|
    | \b 			  | Inserts a backspace in the text at this point. 				|
    | \n 			  | Inserts a newline in the text at this point. 				|
    | \r 		 	  | Inserts a carriage return in the text at this point. 		|
    | \f 			  | Inserts a form feed in the text at this point. 				|
    | \' 			  | Inserts a single quote character in the text at this point. |
    | \" 			  | Inserts a double quote character in the text at this point. |
    | \\ 			  | Inserts a backslash character in the text at this point. 	|

## Java Regex escape sequences

To let Java not look at these as Java escape sequences, the escape symbol has to be doubled. Otherwise Java sees the single backslash and looks for a known escape character, for example a t, r or n. If that does not follow, the Java string gets corrupted.

Not all of the following escape sequences have to be used all the time. The '-', for example, has only a special meaning inside a set like [a-z]. To look for a, b, c, or -, for example, you can use [abc\-].

    | char | Regex Meaning                      | escaped regex | escaped Java  |
    |------|------------------------------------|---------------|---------------|
    |  \   | to look for a single backslash     | \\            | \\\\          |
    |  .   | to look for a period               | \.            | \\.           |
    |  [   | start char class of [a-z]          | \[            | \\[           |
    |  ]   | end char class of [a-z]            | \]            | \\]           |
    |  {   | start quantifier of {N}            | \{            | \\{           |
    |  }   | end quantifier of {N}              | \}            | \\{           |
    |  (   | start group separator              | \(            | \\(           |
    |  )   | end group separator                | \)            | \\)           |
    |  *   | quantifier, zero or many           | \*            | \\*           |
    |  +   | quantifier, one or many            | \+            | \\+           |
    |  -   | set separator in [a-z]             | \-            | \\-           |
    |  ?   | quantifier or back reference       | \?            | \\?           |
    |  ^   | anchor of begin of line            | \^            | \\^           |
    |  $   | groupd reference in replace text   | \$            | \\$           |
    |  |   | 'or' operator                      | \|            | \\|           |

### More examples

If the regex should look for a single digit, the string '\d' is OK for the Java Regex but not as Java string. So it has to be written as '\\d' in the string to let the Regex get a '\d'.

	Regex	Meaning				Java string
	\d		single digit		\\d
	\D		non-digit			\\D
	\s		white space			\\s
	\S		non-white space		\\S
	\w		word char			\\w
	\W		non-word char		\\W
	\b		word boundary		\\b

# Simple Regex

Regex is regular expressions and a way to define a pattern for a search and replace. This demo plugin uses Java Regex.

    .			matches a single symbol
    a			matches a single literal 'a'
    ab			matches the literal sequence 'ab'
    a.c			matches an 'a' followed by any symbol 
           		followed by a literal 'c'
	.*			matches anything (even nothing), the asterisk is a quantifier
				(match as much, can be nothing)
	.+			matches anything (at least one), the plus is a quantifier 
				(match as much, but at least one time)
	*           match no or many times
	+           match one or many times
	?			matches no or one times, short for {0,1}
	{X}			match X times
				\d{3} searches for three digits, 
				.{10} for any character sequence of length 10.
	{X,Y}		matches X to Y times
				\d{1,4} means \d must occur at least once and at a maximum of four.
	*?          finds the smallest match
	^abc		anchors the match to beginning
	abc$		anchors the match to the end
	\(			Java: \\(
				matches a single opening bracket
	\)			Java: \\)
				matches a single closing bracket
	\{  \}		Java: \\{  \\}
	\[  \]		Java: \\[  \\]
				brackets must be escaped if needed to look for	
	.*\(0\).*
				will match a string with "(0)" inside
	(<expr>)	groups the matching expression for later
				reference with $1 to $x, $0 references all groups
				ie: "567" with regex=(\d)(\d)(\d) and replace=$3$1$2 will give
				756
	(a b)		will match the string 'a b' as a group reference
    \u001d		matches the symbol with the unicode 
				hex value 001d
	\t			matches a horizontal tab
	\d			matches a digit, for Java Regex this must be used as \\d
				same as [0-9]
    \D			matches a non-digit, for Java Regex this must be used as \\D
				same as [^0-9]
	\s			matches a white space, for Java Regex this must be used as \\s
				short for [ \t\n\x0b\r\f]
	\w			Java: \\w
				a word char ([a-zA-Z_0-9])
	\W			Java: \\W
				any non-word symbol, [^\w]
	\b			Java: \\b
				a word boundary, where word is [a-zA-Z_0-9] 
	<expr>?		makes expr optional
		^\d+(\.\d+)? Java: "^\\d+(\\.\\d+)?"
				for example matches "5", "1.5", "2.21" 
				but not ".5"
				^ defines that the patter must start at beginning of a new line.
				\d+ matches one or several digits. The ? makes the statement in 
				brackets optional. \. matches ".", parentheses are used for
				grouping
	<expr>{n}	
				quantifier, defines a repetition for the expression
	[abc]		set definition, matches a or b or c
	[abc][vz]	set definition, matches a or b or c followed by v or z
	[^abc]		negated set, matches anything except a or b or c
	[a-d1-7]	ranges, matches a to d or 1 to 7 but not d1
	X|Z			matches X or Z
	XZ			matches X followed by Z
	(\d{2})		matches two digits, ie 01 or 93.
	((ab){3})	matches exactly 'ababab' (as group $2)
	$n			used in replacement for a group reference
	(.)(.)=>$2$1
				will match data consisting of two symbols and replace
				it by exchanging the second and first:
				'ab' will become 'ba'
				'93' will become '39'

	(.{2})(.{5})-(.*)=>$2/$3
				will match data starting with 7 symbols before a '-'
				followed at least one symbol
				this will be replaced by dropping the first group with two symbols, the second found pattern group of 5 symbols, followed by a slash and the third group.
	a(?!b)		negative look ahead ?!, matches a if not followed by b 
	(?i)		makes the regex ignoring case
	(?s)		single line mode
	(?m)		multiline mode

# Examples

The following is a list of examples with barcodes, rules and the resulting wedged data.

In addition to data filtering, the Demo plugin will allow editing of the data once it is accepted.  This editing will allow:

* Splitting the label into fields and extracting these fields for later processing
* Rearrangement or deletion of these fields
* Addition of text to the data
* Stripping of extraneous data.

## Regex Rule Example 1

Purpose: Pass a data string consisting of six digits and add an alphabetic character to the beginning of the string.

    Syntax: ([0-9]{6})=>M$1;

	305481
		will change to "M305481"
	123 305481 123
		will not match, no change with the rule above
  
Thus, a scanned data string such as 305481 would be passed and reformatted to read M305481. A scanned data string consisting of more than six numeric characters will not match.

### Variation A

    Syntax: g=>([0-9]{6})=>M$1;

	305481
		will change to "M305481"
	123 305481 123
		will change to "123 M305481 123"

With the g option, any 6 digit will be replaced by M plus the 6 digits, the rest will not change around.

    Syntax: (.*)\s([0-9]{6})\s(.*)=>M$2;

	305481
		will change to "M305481"
	123 305481 123
		will change to "M305481"

Will find the 6 digits surrounded by white space. A scanned data string in which the six numeric characters are not consecutive would not be passed.

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-6digits.png)

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-3digits_blank_6digits_blank_3digits.png)

## Regex Rule Example 2

Purpose: Pass a data string consisting of nine digits, reformat the data to look like a Social Security number, and add an XML tag called <SSN>.

    Syntax:([0-9]{3})([0-9]{2})([0-9]{4})=><SSN>$1-$2-$3</SSN>;

The regex passes only scanned data strings consisting of a group of three numeric characters, followed by a group of two numeric characters, followed by a group of four numeric characters. Note that the groups are not separated by spaces.

The replacment then reformats the data by adding hyphens between the numeric groups, and finally adds the XML tag "<SSN>" to the beginning of the string and adds "</SSN>" to the end of the string.

Thus, a data string such as:

	123456789

is matched and reformatted to: 

	<SSN>123-45-6789</SSN>. 

A data string such as 1234567890 will not match as the regex will match  be truncated to include only the first nine digits, since it consists of more numeric characters than processed by the regex pattern string.

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-9digits.png)

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-1234567890.png)

## Regex Rule Example 3

Purpose: Pass a data string consisting of 21 digits and delete a specified number of characters from the beginning and end of the string.

	Syntax: ...(.{13}).....=>$1;

The regex passes scanned data strings that consist of 21 characters. The replacement expression removes the first three and the last five characters from the string as these are not part of the group marked with ( and ) in the regex string.

Thus, a scanned data string such as: 

	AAA1234567890123BBBBB 

is passed and reformatted to: 

	1234567890123


![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-AAA1234567890123BBBBB.png)

## Regex Rule Example 4

Purpose: Pass a data string consisting of two groups of alphabetic characters and reformat the string into *lastname*, *firstname*.

	([a-zA-Z]+) (\\w+)=>$2, $1;

The regex matches scanned data strings that consist of two groups of alphabetic characters separated by a space. The replacement expression reformats the passed data string so that the second group of characters precedes the first group. The replacement expression also inserts a comma and a space between the two groups of characters.

The regex uses one set [a-zA-Z] and the word specifier \w to show alternative ways to look for printable characters making a word. In both cases the + sign after the set and specifier means: match at least one and as much as possible.

Thus, a scanned data string such as: 

	Dexter Gordon

is passed, and the data is modified to read: 
	
	Gordon, Dexter

![TotalFreedomTest_02](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/sample-Dexter_Gordon.png)

# Regex Rule Example 5

	g=>([0-9]{6})=>M$1;

The above will replace a sequence of exactly 6 digits by M and the digits.

	10110
		will return no result as the rule does not match
	305481
		will change to "M305481"
	123 305481 123
		will change to "123 M305481 123"
	 

# Addendum

If there is no rule file "dataedit_regex.ini" the plugin will write a default rule file like this:

	# this is a comment;
	# example 1;
	([0-9]{6})=>M$1 ex1;
	# g=>([0-9]{6})=>M$1 ex1a;
	(.*)\\s([0-9]{6})\\s(.*)=>M$2 ex1b;
	# example 2;
	([0-9]{3})([0-9]{2})([0-9]{4})=><SSN>$1-$2-$3</SSN> ex2;
	# example 3;
	...(.{13}).....=>$1 ex3;
	# example 4;
	([a-zA-Z]+) (\\w+)=>$2, $1 ex4;
	(.*)=>no match: $1\n;

The rule lines are the ones mentioned as examples.

For example 1 you should only uncomment one of the example rules:

	([0-9]{6})=>M$1 ex1;
	g=>([0-9]{6})=>M$1 ex1a;
	(.*)\\s([0-9]{6})\\s(.*)=>M$2 ex1b;

or you will get (possibly) unexpected results. With all ex1 rules enabled you will get:

	"123 305481 123" gives "123 M305481 ex1a 123"
		(rule "g=>([0-9]{6})=>M$1 ex1a" matched)
	"305481" gives "M305481 ex1"
		(rule "([0-9]{6})=>M$1 ex1" matched)
## Note

The ; is not part of the rule. ;\r\n is used to split the rules file into rules.

# Testing

There are various web sites that enable online testing of java regex. This should be used to verify a regex will work for given input data and outs pus the desired changed data.
Please note that these links are provided just as a sample and my or may not work for you. There is no warranty.

* myregexp.com

![myregexp](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/myregexcom.png)

* regexplanet.com/advanced/java/

![regexplanet](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/regexplanetcom.png)

* regexe.com

![regexe](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/regexcom.png)

* regex101.com

![regex101](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/regex101com.png)
