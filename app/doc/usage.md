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

## Installation

Install the Demo Data Editing plugin apk either using ADB or by copying the apk file to the device and then use a file browser to install.

### Setup

Go to Settings-Scanning-Internal Scanner-Default Profile-Data Processing Settings-Data Editing Plugin and enter the name of the Totalfreedom Demo DataEdit class:

    hsm.demo.totalfreedom/.DataEdit

![Settings 1](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/DataProcessingSettings_01.png)

![Settings 2](https://raw.githubusercontent.com/hjgode/TotalFreedomDataEdit/master/app/doc/DataProcessingSettings_02.png)

### Android Permissions

As the Plugin uses a shared directory to read the rules file, it needs android.permission.READ_EXTERNAL_STORAGE and android.permission.WRITE_EXTERNAL_STORAGE. Without these permissions the application code does not work. The write permissions is used to write a default, standard rules file as a starter.

### Usage - TotalFreedomTest

After the apk is installed there is also a Test application available called "TotalFreedomTest". The plugin itself does not need an application to run. The test application has an input field, that can be used to show the wedged data and a information area, that shows internals of the plugin work.



## The rules file

The demo plugin tool uses the file
 
    <Device>\Internal storage\Documents\dataedit_regex.ini

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

## A rule line

Every rule line can have either two or three sections or fields. Fields have to be separated by 

    =>

and the rule lines have to end with

    ;\r\n

(A semicolon followed by carriage return / line feed.)
 
### Two-field rules

The first field defines the pattern that a barcode data must match for the rule being executed. The second field is the replacement to be used if the pattern matches the barcode data.

    pattern=>replacement;

### Three-field rules

The first field may contain the option '+' and or 'g' and may be followed by the Symbology AimID that must match the AimID of the scanned barcode.

    [+][g][AimID]=>pattern=>replacement;

#### The '+' option

Rules are processed line by line from top to bottom. If a rule matches, the processing of further rule lines will not take place. If the '+' option is used, the rule processing will not stop after a rule line is matched.

#### The 'g' option

The demo plugin can match a pattern on the whole data or do a simple search-and-replace. If the 'g' option is used, the pattern and replacement field are used for a global search&replace.

#### The AimID

The standard AimID can be used to let the demo plugin match only against barcode that has the same AimID.

For example:

    ]A0=>(.*)=>Aim Id matches for: $1;

will output the data with the prefix "Aim Id matches: " only for barcode with AimID "]A0".

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
