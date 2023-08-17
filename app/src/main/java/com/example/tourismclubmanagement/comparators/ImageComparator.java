package com.example.tourismclubmanagement.comparators;

import com.example.tourismclubmanagement.models.Image;

import java.util.Comparator;
import java.util.List;

public class ImageComparator implements Comparator<Image> {
    private List<Image> images;
    public ImageComparator() {

    }

    @Override
    public int compare(Image image1, Image image2) {
        String reference1 = image1.getReference();
        String reference2 = image2.getReference();

        return reference1.compareToIgnoreCase(reference2);
    }
}
