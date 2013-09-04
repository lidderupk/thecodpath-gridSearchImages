HW 2 - Google image search presented as a grid view. User is able to specify parameters in a preferences activity.

- User is able to search giving 
    - image size
    - image color
    - image website
    - image type (face, clipart, lineart, etc)
- end cases taken care of
    - no input - show toast
- user is able to navigate between result set (8 returned by default)
  - previous button and next button disabled if no input
  - previous button disabled when at start of the search
  - next button disabled when at the end of the search (google only shows upto certain values)
  - toast shown when user hits the end
- preferences remembered per session
