/*
   <!-- Alphabet Hyperlinks Generation  08/30/2000-02/16/2001 -->
   <!-- ------------------------------------------ 02/16/2001 -->
   <!-- www.davar.net/ALPHABET.JS                             -->
   <!-- Copyright (C) 2000-2001 by Vladimir Veytsel           -->
   <!-- Enhanced by Richard Wolschlager 08/30/2005            -->
   <!-- Addition of URL, Set1, and Set2 arguments.            -->
*/   
   // Generates a line of alphabet hyperlinks to be used in text index.
   // Serves merely as a shorthand to save typing as well as page loading
   // time by eliminating massive HTML repetitions.
   
   // Function arguments (required)
   // 1. URL which the link will redirect to.
   // 2. First of a pair of character sets submitted to function.
   //    If you would like to display the entire alphabet enter:
   //         ABCDEFGHIJKLMNOPQRSTUVWXYZ
   // 3. Second of a pair of character sets submitted to function.
   //         ABCDEFGHIJKLMNOPQRSTUVWXYZ
   //    The character sets allow either a single letter per link
   //    or a span of letters per link.  The above entry would list 
   //    a link with each letter.  If the first set is AJS and 
   //    the second set is IRZ.  A link will be created for A-I, J-R,
   //    and S-Z respectively.
   // (optional):
   // 4. Alphabet letter to be skipped (Default: none skipped)
   //    Note: Should be specified at least as an empty placeholder when
   //          the second argument is necessary.
   // 5. Alphabet letters to be deactivated as links (Default: all active)
   
   // The letter to be skipped won't show in the generated alphabet line
   // (used when alphabet line is for the links from this letter).
   
   // The letters to be deactivated as links will be shown as passive (gray)
   // placeholders on the generated alphabet line in contrast with the other
   // letters shown as active links to the letters of the index. This might
   // be necessary for the letters that are not presented in the index.
   
   // The returned value is the HTML for hyperlinked alphabet line to be used
   // in the "document.write".
      
   function ALPHABET()
            {URL=""
             Set1=""
             Set2=""
             Skip=""
             Inactive=""
             if (arguments.length>0)
                URL=arguments[0]
             if (arguments.length>1)
                Set1=arguments[1]
             if (arguments.length>2)
	        Set2=arguments[2]
	     if (arguments.length>3)
	        Skip=arguments[3]
             if (arguments.length>4)
                Inactive=arguments[4]
             Alphabet  ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
             Alpha_Line=""
             for (i=0;i<Set1.length;i++)
                 {Letter1=Set1.substr(i,1)
                  if (i <Set2.length)
                     Letter2=Set2.substr(i,1)
                  else
                     Letter2=Letter1
                  if (Letter1 == Letter2)
                     Span = Letter1
                  else
                     Span = Letter1+"-"+Letter2
                  if ((Alpha_Line!="" )&&  // Suppress ending "|" on the sides
                      (!((Skip   =="Z")&&
                         (Letter1 =="Z"))))
                     Alpha_Line=Alpha_Line+"<FONT COLOR=black>|</FONT>"
                  if (Letter1!=Skip)  // Suppress "Skip" letter (if any)
                     if (Inactive.indexOf(Letter1)==-1)  // Letter1 is active
                        Alpha_Line=Alpha_Line+" <A HREF="+URL+"'"+Letter1+"'%20and%20'"+Letter2+"'>"+Span+"</A>"
                     else  // Letter1 is inactive
                        Alpha_Line=Alpha_Line+" <FONT COLOR=BLUE>"+Span+"</FONT>"
                  if ((Letter1!="Z")&&  // Suppress extra blanks
                      (Letter1!=Skip))
                     Alpha_Line=Alpha_Line+" "
                 }
             return Alpha_Line
            }